package com.sathwikhbhat.soilanalytics.report.dto;

import com.sathwikhbhat.soilanalytics.report.enums.ReportType;
import java.time.LocalDateTime;
import java.util.List;

public record ReportData(String title, ReportType type, LocalDateTime generatedAt, List<ReportSection> sections) {}
