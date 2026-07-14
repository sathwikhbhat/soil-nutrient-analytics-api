package com.sathwikhbhat.soilanalytics.report.service;

import com.sathwikhbhat.soilanalytics.report.dto.ReportData;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public final class ReportResponseBuilder {

    private ReportResponseBuilder() {}

    public static ResponseEntity<Resource> build(byte[] content, ReportData data) {
        String filename = data.type().name().toLowerCase() + "-report.csv";

        ByteArrayResource resource = new ByteArrayResource(content);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header("X-Report-Title", data.title())
                .header("X-Report-Generated-At", data.generatedAt().toString())
                .contentType(new MediaType("text", "csv"))
                .contentLength(content.length)
                .body(resource);
    }
}
