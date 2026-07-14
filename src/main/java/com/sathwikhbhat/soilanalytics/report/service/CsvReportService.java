package com.sathwikhbhat.soilanalytics.report.service;

import com.sathwikhbhat.soilanalytics.exception.FileProcessingException;
import com.sathwikhbhat.soilanalytics.report.dto.ReportData;
import com.sathwikhbhat.soilanalytics.report.dto.ReportSection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CsvReportService {

    public ResponseEntity<Resource> render(ReportData data) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            for (ReportSection section : data.sections()) {
                csvPrinter.printRecord(paddedTitleRow(section));
                csvPrinter.printRecord(section.headers());
                for (var row : section.rows()) {
                    csvPrinter.printRecord(row);
                }
                csvPrinter.println();
            }

            csvPrinter.flush();
            return ReportResponseBuilder.build(outputStream.toByteArray(), data);
        } catch (IOException e) {
            throw new FileProcessingException("Failed to generate CSV report", e);
        }
    }

    private List<String> paddedTitleRow(ReportSection section) {
        List<String> row = new ArrayList<>(Collections.nCopies(section.headers().size(), ""));
        row.set(0, section.title());
        return row;
    }
}
