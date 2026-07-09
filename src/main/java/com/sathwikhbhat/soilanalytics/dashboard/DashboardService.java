package com.sathwikhbhat.soilanalytics.dashboard;

import com.sathwikhbhat.soilanalytics.classification.ClassificationService;
import com.sathwikhbhat.soilanalytics.classification.NutrientLevel;
import com.sathwikhbhat.soilanalytics.classification.dto.NutrientClassificationResponse;
import com.sathwikhbhat.soilanalytics.dashboard.dto.AverageNutrientsResponse;
import com.sathwikhbhat.soilanalytics.dashboard.dto.DashboardOverviewResponse;
import com.sathwikhbhat.soilanalytics.dashboard.dto.DeficiencyPercentageResponse;
import com.sathwikhbhat.soilanalytics.dashboard.dto.NutrientDistributionResponse;
import com.sathwikhbhat.soilanalytics.entity.NutrientData;
import com.sathwikhbhat.soilanalytics.entity.SoilRecord;
import com.sathwikhbhat.soilanalytics.repository.SoilRecordRepository;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final SoilRecordRepository soilRecordRepository;
    private final ClassificationService classificationService;

    public DashboardService(
            DashboardRepository dashboardRepository,
            SoilRecordRepository soilRecordRepository,
            ClassificationService classificationService) {
        this.dashboardRepository = dashboardRepository;
        this.soilRecordRepository = soilRecordRepository;
        this.classificationService = classificationService;
    }

    public DashboardOverviewResponse getDashboardOverview() {
        return new DashboardOverviewResponse(
                dashboardRepository.getTotalSamples(),
                dashboardRepository.getDistrictCoverage(),
                dashboardRepository.getCropCoverage());
    }

    public NutrientDistributionResponse getNutrientDistribution() {
        return new NutrientDistributionResponse(buildNutrientDistribution());
    }

    public DeficiencyPercentageResponse getDeficiencyPercentage() {
        Map<String, Map<NutrientLevel, Integer>> distribution = buildNutrientDistribution();
        long totalSamples = soilRecordRepository.count();

        Map<String, Double> deficiencyPercentage = new HashMap<>();

        if (totalSamples == 0) {
            return new DeficiencyPercentageResponse(deficiencyPercentage);
        }

        for (var entry : distribution.entrySet()) {
            int lowCount = entry.getValue().getOrDefault(NutrientLevel.LOW, 0);
            double percentage = (lowCount * 100.0) / totalSamples;

            deficiencyPercentage.put(entry.getKey(), percentage);
        }

        return new DeficiencyPercentageResponse(deficiencyPercentage);
    }

    public AverageNutrientsResponse getAverageNutrients() {
        List<SoilRecord> soilRecords = soilRecordRepository.findAll();

        if (soilRecords.isEmpty()) {
            return new AverageNutrientsResponse(new HashMap<>());
        }

        Map<String, Double> averageNutrients = new HashMap<>();

        for (SoilRecord records : soilRecords) {
            NutrientData nutrients = records.getNutrients();

            merge(averageNutrients, "ph", nutrients.ph());
            merge(averageNutrients, "ec", nutrients.ec());
            merge(averageNutrients, "organicCarbon", nutrients.organicCarbon());
            merge(averageNutrients, "nitrogen", nutrients.nitrogen());
            merge(averageNutrients, "phosphorus", nutrients.phosphorus());
            merge(averageNutrients, "potassium", nutrients.potassium());
            merge(averageNutrients, "sulfur", nutrients.sulfur());
            merge(averageNutrients, "zinc", nutrients.zinc());
            merge(averageNutrients, "boron", nutrients.boron());
            merge(averageNutrients, "iron", nutrients.iron());
            merge(averageNutrients, "copper", nutrients.copper());
            merge(averageNutrients, "manganese", nutrients.manganese());
        }

        int totalSamples = soilRecords.size();

        averageNutrients.replaceAll((nutrient, sum) -> sum / totalSamples);

        return new AverageNutrientsResponse(averageNutrients);
    }

    private void merge(Map<String, Double> averageNutrients, String nutrient, double value) {
        averageNutrients.merge(nutrient, value, Double::sum);
    }

    private Map<String, Map<NutrientLevel, Integer>> buildNutrientDistribution() {
        Map<String, Map<NutrientLevel, Integer>> distribution = new HashMap<>();

        List<SoilRecord> soilRecords = soilRecordRepository.findAll();

        for (SoilRecord records : soilRecords) {
            NutrientClassificationResponse nutrientData = classificationService.classify(records.getNutrients());

            merge(distribution, "ph", nutrientData.ph());
            merge(distribution, "ec", nutrientData.ec());
            merge(distribution, "organicCarbon", nutrientData.organicCarbon());
            merge(distribution, "nitrogen", nutrientData.nitrogen());
            merge(distribution, "phosphorus", nutrientData.phosphorus());
            merge(distribution, "potassium", nutrientData.potassium());
            merge(distribution, "sulfur", nutrientData.sulfur());
            merge(distribution, "zinc", nutrientData.zinc());
            merge(distribution, "boron", nutrientData.boron());
            merge(distribution, "iron", nutrientData.iron());
            merge(distribution, "copper", nutrientData.copper());
            merge(distribution, "manganese", nutrientData.manganese());
        }

        return distribution;
    }

    private void merge(Map<String, Map<NutrientLevel, Integer>> distribution, String nutrient, NutrientLevel level) {
        distribution
                .computeIfAbsent(nutrient, k -> new EnumMap<>(NutrientLevel.class))
                .merge(level, 1, Integer::sum);
    }
}
