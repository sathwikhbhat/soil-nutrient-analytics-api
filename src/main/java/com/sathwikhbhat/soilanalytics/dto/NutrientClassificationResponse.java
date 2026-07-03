package com.sathwikhbhat.soilanalytics.dto;

import com.sathwikhbhat.soilanalytics.classification.NutrientLevel;

public record NutrientClassificationResponse(
        NutrientLevel ph,
        NutrientLevel ec,
        NutrientLevel organicCarbon,
        NutrientLevel nitrogen,
        NutrientLevel phosphorus,
        NutrientLevel potassium,
        NutrientLevel sulfur,
        NutrientLevel zinc,
        NutrientLevel boron,
        NutrientLevel iron,
        NutrientLevel copper,
        NutrientLevel manganese) {}
