package com.sathwikhbhat.soilanalytics.report.util;

import com.sathwikhbhat.soilanalytics.classification.NutrientLevel;
import com.sathwikhbhat.soilanalytics.classification.dto.NutrientClassificationResponse;
import com.sathwikhbhat.soilanalytics.entity.NutrientData;
import java.util.List;

public final class NutrientFields {

    public static final List<String> ALL_NUTRIENTS = List.of(
            "ph",
            "ec",
            "organicCarbon",
            "nitrogen",
            "phosphorus",
            "potassium",
            "sulfur",
            "zinc",
            "boron",
            "iron",
            "copper",
            "manganese");

    public static final List<String> FERTILITY_NUTRIENTS =
            List.of("ph", "organicCarbon", "nitrogen", "phosphorus", "potassium");

    private NutrientFields() {}

    public static double getValue(NutrientData nutrients, String nutrient) {
        return switch (nutrient) {
            case "ph" -> nutrients.ph();
            case "ec" -> nutrients.ec();
            case "organicCarbon" -> nutrients.organicCarbon();
            case "nitrogen" -> nutrients.nitrogen();
            case "phosphorus" -> nutrients.phosphorus();
            case "potassium" -> nutrients.potassium();
            case "sulfur" -> nutrients.sulfur();
            case "zinc" -> nutrients.zinc();
            case "boron" -> nutrients.boron();
            case "iron" -> nutrients.iron();
            case "copper" -> nutrients.copper();
            case "manganese" -> nutrients.manganese();
            default -> throw new IllegalArgumentException("Unknown nutrient: " + nutrient);
        };
    }

    public static NutrientLevel getLevel(NutrientClassificationResponse classification, String nutrient) {
        return switch (nutrient) {
            case "ph" -> classification.ph();
            case "ec" -> classification.ec();
            case "organicCarbon" -> classification.organicCarbon();
            case "nitrogen" -> classification.nitrogen();
            case "phosphorus" -> classification.phosphorus();
            case "potassium" -> classification.potassium();
            case "sulfur" -> classification.sulfur();
            case "zinc" -> classification.zinc();
            case "boron" -> classification.boron();
            case "iron" -> classification.iron();
            case "copper" -> classification.copper();
            case "manganese" -> classification.manganese();
            default -> throw new IllegalArgumentException("Unknown nutrient: " + nutrient);
        };
    }
}
