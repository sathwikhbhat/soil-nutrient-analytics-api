package com.sathwikhbhat.soilanalytics.dto;

import com.sathwikhbhat.soilanalytics.entity.Location;
import com.sathwikhbhat.soilanalytics.entity.NutrientData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record SoilRecordRequest(
        @NotBlank String sampleId,
        @NotNull Location location,
        @NotBlank String crop,
        @NotNull LocalDate testDate,
        @NotNull NutrientData nutrients) {}
