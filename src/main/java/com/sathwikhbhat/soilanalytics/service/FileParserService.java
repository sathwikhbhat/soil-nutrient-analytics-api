package com.sathwikhbhat.soilanalytics.service;

import com.sathwikhbhat.soilanalytics.dto.SoilRecordRequest;
import com.sathwikhbhat.soilanalytics.entity.Location;
import com.sathwikhbhat.soilanalytics.entity.NutrientData;
import com.sathwikhbhat.soilanalytics.exception.FileProcessingException;
import com.sathwikhbhat.soilanalytics.exception.UnsupportedFileTypeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileParserService {

    private final SoilRecordService soilRecordService;

    public FileParserService(SoilRecordService soilRecordService) {
        this.soilRecordService = soilRecordService;
    }

    public void importFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if (file.isEmpty() || fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            throw new UnsupportedFileTypeException("Unsupported file type. Please upload a CSV file.");
        }

        try {
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

            CSVParser parser = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get()
                    .parse(reader);

            for (CSVRecord csvRecord : parser) {
                Location location = new Location(
                        csvRecord.get("state"),
                        csvRecord.get("district"),
                        csvRecord.get("taluk"),
                        csvRecord.get("village"),
                        Double.parseDouble(csvRecord.get("latitude")),
                        Double.parseDouble(csvRecord.get("longitude")));

                NutrientData nutrients = new NutrientData(
                        Double.parseDouble(csvRecord.get("ph")),
                        Double.parseDouble(csvRecord.get("ec")),
                        Double.parseDouble(csvRecord.get("organicCarbon")),
                        Double.parseDouble(csvRecord.get("nitrogen")),
                        Double.parseDouble(csvRecord.get("phosphorus")),
                        Double.parseDouble(csvRecord.get("potassium")),
                        Double.parseDouble(csvRecord.get("sulfur")),
                        Double.parseDouble(csvRecord.get("zinc")),
                        Double.parseDouble(csvRecord.get("boron")),
                        Double.parseDouble(csvRecord.get("iron")),
                        Double.parseDouble(csvRecord.get("copper")),
                        Double.parseDouble(csvRecord.get("manganese")));

                SoilRecordRequest request = new SoilRecordRequest(
                        csvRecord.get("sampleId"),
                        location,
                        csvRecord.get("crop"),
                        LocalDate.parse(csvRecord.get("testDate")),
                        nutrients);

                soilRecordService.create(request);
            }
        } catch (IOException e) {
            throw new FileProcessingException("Failed to process CSV file.", e);
        }
    }
}
