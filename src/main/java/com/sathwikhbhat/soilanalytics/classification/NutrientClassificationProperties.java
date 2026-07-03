package com.sathwikhbhat.soilanalytics.classification;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nutrient-classification")
public record NutrientClassificationProperties(
        NutrientThreshold ph,
        NutrientThreshold ec,
        NutrientThreshold organicCarbon,
        NutrientThreshold nitrogen,
        NutrientThreshold phosphorus,
        NutrientThreshold potassium,
        NutrientThreshold sulfur,
        NutrientThreshold zinc,
        NutrientThreshold boron,
        NutrientThreshold iron,
        NutrientThreshold copper,
        NutrientThreshold manganese) {}
