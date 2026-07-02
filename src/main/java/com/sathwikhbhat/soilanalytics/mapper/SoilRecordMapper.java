package com.sathwikhbhat.soilanalytics.mapper;

import com.sathwikhbhat.soilanalytics.dto.SoilRecordRequest;
import com.sathwikhbhat.soilanalytics.dto.SoilRecordResponse;
import com.sathwikhbhat.soilanalytics.entity.SoilRecord;

public class SoilRecordMapper {
    private SoilRecordMapper() {
        /* This utility class should not be instantiated */
    }

    public static SoilRecord toEntity(SoilRecordRequest request) {
        return SoilRecord.builder()
                .sampleId(request.sampleId())
                .location(request.location())
                .crop(request.crop())
                .testDate(request.testDate())
                .nutrients(request.nutrients())
                .build();
    }

    public static SoilRecordResponse toResponse(SoilRecord entity) {
        return new SoilRecordResponse(
                entity.getId(),
                entity.getSampleId(),
                entity.getLocation(),
                entity.getCrop(),
                entity.getTestDate(),
                entity.getNutrients(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
