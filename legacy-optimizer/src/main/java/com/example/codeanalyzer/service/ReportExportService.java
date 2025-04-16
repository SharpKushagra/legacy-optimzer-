package com.example.codeanalyzer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;

@Service
public class ReportExportService {

    public byte[] exportToJson(Map<String, Object> analysisResult) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writerWithDefaultPrettyPrinter().writeValue(out, analysisResult);
        return out.toByteArray();
    }

    public byte[] exportToPdf(Map<String, Object> analysisResult) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        document.add(new Paragraph("Java Code Analysis Report", titleFont));
        document.add(Chunk.NEWLINE);

        for (Map.Entry<String, Object> entry : analysisResult.entrySet()) {
            document.add(new Paragraph(entry.getKey() + ": " + entry.getValue(), normalFont));
        }

        document.close();
        return out.toByteArray();
    }
}
