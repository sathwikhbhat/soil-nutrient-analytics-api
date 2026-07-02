package com.sathwikhbhat.soilanalytics.dto;

import com.sathwikhbhat.soilanalytics.entity.Location;
import com.sathwikhbhat.soilanalytics.entity.NutrientData;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SoilRecordResponse(
        String id,
        String sampleId,
        Location location,
        String crop,
        LocalDate testDate,
        NutrientData nutrients,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
