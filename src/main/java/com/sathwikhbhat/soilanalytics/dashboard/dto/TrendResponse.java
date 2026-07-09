package com.sathwikhbhat.soilanalytics.dashboard.dto;

import java.util.Map;

public record TrendResponse(Map<String, Map<String, Double>> trends) {}
