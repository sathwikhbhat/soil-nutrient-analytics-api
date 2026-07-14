package com.sathwikhbhat.soilanalytics.soilrecord.entity;

import jakarta.validation.constraints.NotNull;

public record NutrientData(
        @NotNull Double ph,
        @NotNull Double ec,
        @NotNull Double organicCarbon,
        @NotNull Double nitrogen,
        @NotNull Double phosphorus,
        @NotNull Double potassium,
        @NotNull Double sulfur,
        @NotNull Double zinc,
        @NotNull Double boron,
        @NotNull Double iron,
        @NotNull Double copper,
        @NotNull Double manganese) {}
