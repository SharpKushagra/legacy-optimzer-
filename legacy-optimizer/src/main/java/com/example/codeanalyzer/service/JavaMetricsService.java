package com.example.codeanalyzer.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class JavaMetricsService {

    public Map<String, Object> analyzeMetrics(File file) {
        Map<String, Object> metrics = new HashMap<>();
        int classCount = 0;
        int methodCount = 0;
        int fieldCount = 0;
        int lineCount = 0;

        List<String> longMethods = new ArrayList<>();
        List<String> codeSmells = new ArrayList<>();

        try (FileInputStream in = new FileInputStream(file)) {
            JavaParser parser = new JavaParser();
            ParseResult<CompilationUnit> result = parser.parse(in);

            if (result.isSuccessful() && result.getResult().isPresent()) {
                CompilationUnit cu = result.getResult().get();

                List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
                classCount = classes.size();

                List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
                methodCount = methods.size();

                List<FieldDeclaration> fields = cu.findAll(FieldDeclaration.class);
                fieldCount = fields.size();

                lineCount = cu.getRange()
                        .map(r -> r.end.line - r.begin.line + 1)
                        .orElse(0);

                for (MethodDeclaration method : methods) {
                    int begin = method.getBegin().map(p -> p.line).orElse(0);
                    int end = method.getEnd().map(p -> p.line).orElse(0);
                    int lines = end - begin + 1;

                    if (lines > 50) {
                        String methodInfo = method.getNameAsString() + " (" + lines + " lines)";
                        longMethods.add(methodInfo);
                        codeSmells.add("Long Method: " + methodInfo);
                    }
                }

                for (ClassOrInterfaceDeclaration cls : classes) {
                    int begin = cls.getBegin().map(p -> p.line).orElse(0);
                    int end = cls.getEnd().map(p -> p.line).orElse(0);
                    int classLines = end - begin + 1;

                    if (classLines > 1000) {
                        String classInfo = cls.getNameAsString() + " (" + classLines + " lines)";
                        codeSmells.add("Large Class: " + classInfo);
                    }
                }

                metrics.put("classes", classCount);
                metrics.put("methods", methodCount);
                metrics.put("fields", fieldCount);
                metrics.put("linesOfCode", lineCount);
                metrics.put("longMethodsCount", longMethods.size());
                metrics.put("longMethods", longMethods);
                metrics.put("codeSmells", codeSmells);
            } else {
                System.out.println("Parsing failed: " + result.getProblems());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return metrics;
    }
}
