package com.sathwikhbhat.soilanalytics.controller;

import com.sathwikhbhat.soilanalytics.dto.NutrientClassificationResponse;
import com.sathwikhbhat.soilanalytics.dto.SoilRecordRequest;
import com.sathwikhbhat.soilanalytics.dto.SoilRecordResponse;
import com.sathwikhbhat.soilanalytics.service.FileParserService;
import com.sathwikhbhat.soilanalytics.service.SoilRecordService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/soil-records")
public class SoilRecordController {

    private final SoilRecordService soilRecordService;
    private final FileParserService fileParserService;

    public SoilRecordController(SoilRecordService soilRecordService, FileParserService fileParserService) {
        this.soilRecordService = soilRecordService;
        this.fileParserService = fileParserService;
    }

    @PostMapping
    public ResponseEntity<SoilRecordResponse> createSoilRecord(@Valid @RequestBody SoilRecordRequest request) {
        SoilRecordResponse response = soilRecordService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SoilRecordResponse>> getAllSoilRecords() {
        List<SoilRecordResponse> responses = soilRecordService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SoilRecordResponse> getSoilRecordById(@PathVariable String id) {
        SoilRecordResponse response = soilRecordService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SoilRecordResponse> updateSoilRecord(
            @PathVariable String id, @Valid @RequestBody SoilRecordRequest request) {
        SoilRecordResponse response = soilRecordService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSoilRecord(@PathVariable String id) {
        soilRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam MultipartFile file) {
        fileParserService.importFile(file);
        return ResponseEntity.ok("File uploaded and processed successfully.");
    }

    @GetMapping("/{id}/classification")
    public ResponseEntity<NutrientClassificationResponse> getNutrientClassification(@PathVariable String id) {
        NutrientClassificationResponse nutrientClassificationResponse = soilRecordService.getNutrientClassification(id);
        return ResponseEntity.ok(nutrientClassificationResponse);
    }
}
