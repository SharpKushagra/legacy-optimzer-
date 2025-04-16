package com.example.codeanalyzer.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.expr.BinaryExpr;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class CyclomaticComplexityService {

    public Map<String, Integer> calculateComplexity(File file) {
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
                complexity += method.findAll(ConditionalExpr.class).size(); // ternary
                complexity += method.findAll(BinaryExpr.class).stream()
                        .filter(be -> be.getOperator() == BinaryExpr.Operator.AND
                                || be.getOperator() == BinaryExpr.Operator.OR)
                        .count();

                complexityMap.put(method.getNameAsString(), complexity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return complexityMap;
    }
}
