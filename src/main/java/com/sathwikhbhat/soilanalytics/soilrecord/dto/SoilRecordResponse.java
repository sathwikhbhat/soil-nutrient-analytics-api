package com.sathwikhbhat.soilanalytics.soilrecord.dto;

import com.sathwikhbhat.soilanalytics.soilrecord.entity.Location;
import com.sathwikhbhat.soilanalytics.soilrecord.entity.NutrientData;
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
