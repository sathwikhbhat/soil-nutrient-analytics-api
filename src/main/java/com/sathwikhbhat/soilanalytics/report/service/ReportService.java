package com.sathwikhbhat.soilanalytics.report.service;

import com.sathwikhbhat.soilanalytics.report.dto.ReportData;
import com.sathwikhbhat.soilanalytics.report.enums.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportDataService reportDataService;
    private final CsvReportService csvReportService;

    public ResponseEntity<Resource> generateReport(ReportType type) {
        ReportData reportData = reportDataService.getReportData(type);

        return csvReportService.render(reportData);
    }
}
