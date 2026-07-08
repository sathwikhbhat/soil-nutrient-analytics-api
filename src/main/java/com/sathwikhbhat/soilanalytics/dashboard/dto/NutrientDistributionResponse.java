package com.sathwikhbhat.soilanalytics.dashboard.dto;

import com.sathwikhbhat.soilanalytics.classification.NutrientLevel;
import java.util.Map;

public record NutrientDistributionResponse(Map<String, Map<NutrientLevel, Integer>> distribution) {}
