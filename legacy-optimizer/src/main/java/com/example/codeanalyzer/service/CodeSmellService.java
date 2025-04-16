package com.example.codeanalyzer.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
@Service
public class CodeSmellService {

    public Map<String, Integer> calculateCyclomaticComplexity(File file) {
        Map<String, Integer> complexityMap = new HashMap<>();

        try (FileInputStream in = new FileInputStream(file)) {
            JavaParser parser = new JavaParser();
            CompilationUnit cu = parser.parse(in).getResult().orElse(null);
            if (cu == null) return complexityMap;

            for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
                int complexity = 1;
                complexity += method.findAll(IfStmt.class).size();
                complexity += method.findAll(ForStmt.class).size();
                complexity += method.findAll(ForEachStmt.class).size();
                complexity += method.findAll(WhileStmt.class).size();
                complexity += method.findAll(DoStmt.class).size();
                complexity += method.findAll(SwitchEntry.class).stream()
                        .mapToInt(entry -> entry.getLabels().size())
                        .sum();
                complexity += method.findAll(CatchClause.class).size();
                complexity += method.findAll(ConditionalExpr.class).size();
                complexityMap.put(method.getNameAsString(), complexity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return complexityMap;
    }

    public Map<String, List<String>> detectRefactoringOpportunities(File file) {
        Map<String, List<String>> suggestions = new HashMap<>();
        List<String> longMethods = new ArrayList<>();
        List<String> largeClasses = new ArrayList<>();

        try (FileInputStream in = new FileInputStream(file)) {
            JavaParser parser = new JavaParser();
            CompilationUnit cu = parser.parse(in).getResult().orElse(null);
            if (cu == null) return suggestions;

            for (ClassOrInterfaceDeclaration clazz : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                int classBegin = clazz.getBegin().map(p -> p.line).orElse(0);
                int classEnd = clazz.getEnd().map(p -> p.line).orElse(0);
                int classLines = classEnd - classBegin + 1;
                int methodCount = clazz.findAll(MethodDeclaration.class).size();

                if (classLines > 1000 || methodCount > 20) {
                    largeClasses.add(clazz.getNameAsString() + " (" + classLines + " lines, " + methodCount + " methods)");
                }

                for (MethodDeclaration method : clazz.findAll(MethodDeclaration.class)) {
                    int begin = method.getBegin().map(p -> p.line).orElse(0);
                    int end = method.getEnd().map(p -> p.line).orElse(0);
                    int lines = end - begin + 1;

                    if (lines > 50) {
                        longMethods.add(method.getNameAsString() + " (" + lines + " lines)");
                    }
                }
            }

            suggestions.put("LongMethod", longMethods);
            suggestions.put("LargeClass", largeClasses);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return suggestions;
    }
    public List<String> detectDuplicateMethods(File file) {
        List<String> duplicates = new ArrayList<>();
        Map<String, String> methodBodies = new HashMap<>();
    
        try (FileInputStream in = new FileInputStream(file)) {
            JavaParser parser = new JavaParser();
            CompilationUnit cu = parser.parse(in).getResult().orElse(null);
            if (cu == null) return duplicates;
    
            for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
                String methodName = method.getNameAsString();
                String body = method.getBody().map(Object::toString).orElse("");
    
                for (Map.Entry<String, String> entry : methodBodies.entrySet()) {
                    if (entry.getValue().equals(body)) {
                        duplicates.add("Duplicate methods: " + methodName + " and " + entry.getKey());
                    }
                }
                methodBodies.put(methodName, body);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return duplicates;
    }
    public int estimateTechnicalDebt(File file) {
        int debtScore = 0;
    
        try (FileInputStream in = new FileInputStream(file)) {
            JavaParser parser = new JavaParser();
            CompilationUnit cu = parser.parse(in).getResult().orElse(null);
            if (cu == null) return 0;
    
            List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
    
            for (MethodDeclaration method : methods) {
                int start = method.getBegin().map(p -> p.line).orElse(0);
                int end = method.getEnd().map(p -> p.line).orElse(0);
                int length = end - start + 1;
    
                if (length > 50) debtScore += 5; // long method penalty
                if (length > 100) debtScore += 10;
    
                int complexity = 1;
                complexity += method.findAll(IfStmt.class).size();
                complexity += method.findAll(ForStmt.class).size();
                complexity += method.findAll(ForEachStmt.class).size();
                complexity += method.findAll(WhileStmt.class).size();
                complexity += method.findAll(DoStmt.class).size();
                complexity += method.findAll(SwitchEntry.class).stream()
                        .mapToInt(entry -> entry.getLabels().size())
                        .sum();
                complexity += method.findAll(CatchClause.class).size();
                complexity += method.findAll(ConditionalExpr.class).size();
    
                if (complexity > 10) debtScore += 5;
                if (complexity > 20) debtScore += 10;
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return debtScore;
    }
}    
