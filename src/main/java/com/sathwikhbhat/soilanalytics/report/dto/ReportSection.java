package com.sathwikhbhat.soilanalytics.report.dto;

import java.util.List;

public record ReportSection(String title, List<String> headers, List<List<String>> rows) {}
