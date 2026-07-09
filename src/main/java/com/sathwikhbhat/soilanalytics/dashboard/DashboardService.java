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

        if (totalSamples == 0) {
            return new DeficiencyPercentageResponse(new HashMap<>());
        }

        Map<String, Double> deficiencyPercentage = new HashMap<>();

        for (var entry : distribution.entrySet()) {
            int lowCount = entry.getValue().getOrDefault(NutrientLevel.LOW, 0);
            deficiencyPercentage.put(entry.getKey(), (lowCount * 100.0) / totalSamples);
        }

        return new DeficiencyPercentageResponse(deficiencyPercentage);
    }

    public AverageNutrientsResponse getAverageNutrients() {
        List<SoilRecord> soilRecords = soilRecordRepository.findAll();

        if (soilRecords.isEmpty()) {
            return new AverageNutrientsResponse(new HashMap<>());
        }

        Map<String, Double> averageNutrients = new HashMap<>();

        for (SoilRecord record : soilRecords) {
            accumulateAverageNutrients(averageNutrients, record.getNutrients());
        }

        averageNutrients.replaceAll((nutrient, sum) -> sum / soilRecords.size());

        return new AverageNutrientsResponse(averageNutrients);
    }

    private Map<String, Map<NutrientLevel, Integer>> buildNutrientDistribution() {
        Map<String, Map<NutrientLevel, Integer>> distribution = new HashMap<>();

        List<SoilRecord> soilRecords = soilRecordRepository.findAll();

        for (SoilRecord record : soilRecords) {
            NutrientClassificationResponse classification = classificationService.classify(record.getNutrients());

            accumulateDistribution(distribution, classification);
        }

        return distribution;
    }

    private void accumulateAverageNutrients(Map<String, Double> averageNutrients, NutrientData nutrients) {

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

    private void accumulateDistribution(
            Map<String, Map<NutrientLevel, Integer>> distribution, NutrientClassificationResponse classification) {

        merge(distribution, "ph", classification.ph());
        merge(distribution, "ec", classification.ec());
        merge(distribution, "organicCarbon", classification.organicCarbon());
        merge(distribution, "nitrogen", classification.nitrogen());
        merge(distribution, "phosphorus", classification.phosphorus());
        merge(distribution, "potassium", classification.potassium());
        merge(distribution, "sulfur", classification.sulfur());
        merge(distribution, "zinc", classification.zinc());
        merge(distribution, "boron", classification.boron());
        merge(distribution, "iron", classification.iron());
        merge(distribution, "copper", classification.copper());
        merge(distribution, "manganese", classification.manganese());
    }

    private void merge(Map<String, Double> averageNutrients, String nutrient, double value) {
        averageNutrients.merge(nutrient, value, Double::sum);
    }

    private void merge(Map<String, Map<NutrientLevel, Integer>> distribution, String nutrient, NutrientLevel level) {
        distribution
                .computeIfAbsent(nutrient, k -> new EnumMap<>(NutrientLevel.class))
                .merge(level, 1, Integer::sum);
    }
}
