package com.sathwikhbhat.soilanalytics.dto;

import com.sathwikhbhat.soilanalytics.entity.Location;
import com.sathwikhbhat.soilanalytics.entity.NutrientData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record SoilRecordRequest(
        @NotBlank String sampleId,
        @NotNull @Valid Location location,
        @NotBlank String crop,
        @NotNull LocalDate testDate,
        @NotNull @Valid NutrientData nutrients) {}
