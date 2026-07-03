package com.sathwikhbhat.soilanalytics.service;

import com.sathwikhbhat.soilanalytics.classification.ClassificationService;
import com.sathwikhbhat.soilanalytics.dto.NutrientClassificationResponse;
import com.sathwikhbhat.soilanalytics.dto.SoilRecordRequest;
import com.sathwikhbhat.soilanalytics.dto.SoilRecordResponse;
import com.sathwikhbhat.soilanalytics.entity.SoilRecord;
import com.sathwikhbhat.soilanalytics.exception.SoilRecordNotFoundException;
import com.sathwikhbhat.soilanalytics.mapper.SoilRecordMapper;
import com.sathwikhbhat.soilanalytics.repository.SoilRecordRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SoilRecordService {

    private final SoilRecordRepository soilRecordRepository;
    private final ClassificationService classificationService;

    @Value("${app.timezone}")
    private String timezone;

    public SoilRecordService(SoilRecordRepository soilRecordRepository, ClassificationService classificationService) {
        this.soilRecordRepository = soilRecordRepository;
        this.classificationService = classificationService;
    }

    public SoilRecordResponse create(SoilRecordRequest request) {
        SoilRecord soilRecord = SoilRecordMapper.toEntity(request);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        soilRecord.setCreatedAt(now);
        soilRecord.setUpdatedAt(now);

        SoilRecord savedRecord = soilRecordRepository.save(soilRecord);

        return SoilRecordMapper.toResponse(savedRecord);
    }

    public List<SoilRecordResponse> getAll() {
        List<SoilRecord> soilRecords = soilRecordRepository.findAll();
        return soilRecords.stream().map(SoilRecordMapper::toResponse).toList();
    }

    public SoilRecordResponse getById(String id) {
        SoilRecord soilRecord = soilRecordRepository
                .findById(id)
                .orElseThrow(() -> new SoilRecordNotFoundException("Soil record not found with id: " + id));
        return SoilRecordMapper.toResponse(soilRecord);
    }

    public SoilRecordResponse update(String id, SoilRecordRequest request) {
        SoilRecord existingRecord = soilRecordRepository
                .findById(id)
                .orElseThrow(() -> new SoilRecordNotFoundException("Soil record not found with id: " + id));
        SoilRecord updatedRecord = SoilRecordMapper.toEntity(request);

        updatedRecord.setId(id);
        updatedRecord.setCreatedAt(existingRecord.getCreatedAt());
        updatedRecord.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

        SoilRecord savedRecord = soilRecordRepository.save(updatedRecord);
        return SoilRecordMapper.toResponse(savedRecord);
    }

    public void delete(String id) {
        soilRecordRepository.deleteById(id);
    }

    public NutrientClassificationResponse getNutrientClassification(String id) {
        SoilRecord soilRecord = soilRecordRepository
                .findById(id)
                .orElseThrow(() -> new SoilRecordNotFoundException("Soil record not found with id: " + id));
        return classificationService.classify(soilRecord.getNutrients());
    }
}
