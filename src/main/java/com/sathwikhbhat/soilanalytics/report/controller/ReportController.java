package com.sathwikhbhat.soilanalytics.report.controller;

import com.sathwikhbhat.soilanalytics.exception.InvalidReportTypeException;
import com.sathwikhbhat.soilanalytics.report.enums.ReportType;
import com.sathwikhbhat.soilanalytics.report.service.ReportService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<Resource> generateReport(@RequestParam String type) {
        return reportService.generateReport(parseReportType(type));
    }

    private ReportType parseReportType(String type) {
        try {
            return ReportType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidReportTypeException(type);
        }
    }
}
