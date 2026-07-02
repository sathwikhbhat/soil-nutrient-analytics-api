package com.sathwikhbhat.soilanalytics.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "soil_records")
public class SoilRecord {

    String id;
    String sampleId;
    Location location;
    String crop;
    LocalDate testDate;
    NutrientData nutrients;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
