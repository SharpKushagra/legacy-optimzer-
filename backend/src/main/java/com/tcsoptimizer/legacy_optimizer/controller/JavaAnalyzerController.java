package com.tcsoptimizer.legacy_optimizer.controller;

import com.example.codeanalyzer.service.CodeSmellService;
import com.example.codeanalyzer.service.ReportExportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analyze")
public class JavaAnalyzerController {

    private final CodeSmellService codeSmellService;
    private final ReportExportService reportExportService;

    public JavaAnalyzerController(CodeSmellService codeSmellService, ReportExportService reportExportService) {
        this.codeSmellService = codeSmellService;
        this.reportExportService = reportExportService;
    }

    // ✅ Test endpoint for frontend-backend connection
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Backend is working!");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/java", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> analyzeJavaFile(@RequestParam("file") MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            String content = new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));

            File tempFile = File.createTempFile("uploaded", ".java");
            file.transferTo(tempFile);

            Pattern packagePattern = Pattern.compile("package\\s+([a-zA-Z0-9_.]+);");
            Pattern importPattern = Pattern.compile("import\\s+([a-zA-Z0-9_.]+);");
            Pattern classPattern = Pattern.compile("class\\s+(\\w+)");
            Pattern methodPattern = Pattern.compile("(public|protected|private|static|\\s)+[\\w<>\\[\\]]+\\s+(\\w+)\\s*\\(");

            Matcher packageMatcher = packagePattern.matcher(content);
            Matcher importMatcher = importPattern.matcher(content);
            Matcher classMatcher = classPattern.matcher(content);
            Matcher methodMatcher = methodPattern.matcher(content);

            String packageName = packageMatcher.find() ? packageMatcher.group(1) : "N/A";

            List<String> imports = new ArrayList<>();
            while (importMatcher.find()) {
                imports.add(importMatcher.group(1));
            }

            List<String> classNames = new ArrayList<>();
            while (classMatcher.find()) {
                classNames.add(classMatcher.group(1));
            }

            List<String> methodNames = new ArrayList<>();
            while (methodMatcher.find()) {
                methodNames.add(methodMatcher.group(2));
            }

            int lineCount = content.split("\n").length;

            Map<String, Integer> codeSmells = codeSmellService.calculateCyclomaticComplexity(tempFile);
            List<String> duplicateMethods = codeSmellService.detectDuplicateMethods(tempFile);
            int technicalDebtScore = codeSmellService.estimateTechnicalDebt(tempFile);

            Map<String, Object> response = new HashMap<>();
            response.put("filename", filename);
            response.put("package", packageName);
            response.put("imports", imports);
            response.put("classes", classNames);
            response.put("methods", methodNames);
            response.put("totalLines", lineCount);
            response.put("cyclomaticComplexity", codeSmells);
            response.put("duplicateMethods", duplicateMethods);
            response.put("technicalDebtScore", technicalDebtScore);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to analyze Java file", "details", e.getMessage()));
        }
    }

    @PostMapping(value = "/export/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportJson(@RequestBody Map<String, Object> analysisData) {
        try {
            byte[] jsonBytes = reportExportService.exportToJson(analysisData);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.json")
                    .body(jsonBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Failed to export JSON: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping(value = "/export/pdf", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportPdf(@RequestBody Map<String, Object> analysisData) {
        try {
            byte[] pdfBytes = reportExportService.exportToPdf(analysisData);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Failed to export PDF: " + e.getMessage()).getBytes());
        }
    }
}
