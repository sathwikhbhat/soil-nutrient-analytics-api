package com.sathwikhbhat.soilanalytics.dashboard.service;

import com.sathwikhbhat.soilanalytics.classification.ClassificationService;
import com.sathwikhbhat.soilanalytics.classification.dto.NutrientClassificationResponse;
import com.sathwikhbhat.soilanalytics.classification.model.NutrientLevel;
import com.sathwikhbhat.soilanalytics.dashboard.dto.*;
import com.sathwikhbhat.soilanalytics.dashboard.repository.DashboardRepository;
import com.sathwikhbhat.soilanalytics.soilrecord.entity.NutrientData;
import com.sathwikhbhat.soilanalytics.soilrecord.entity.SoilRecord;
import com.sathwikhbhat.soilanalytics.soilrecord.repository.SoilRecordRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
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

    public TrendResponse getTrends(TrendType type) {
        List<SoilRecord> soilRecords = soilRecordRepository.findAll();

        if (soilRecords.isEmpty()) {
            return new TrendResponse(new HashMap<>());
        }

        Map<String, TrendAccumulator> groupedData = new HashMap<>();

        for (SoilRecord record : soilRecords) {
            String key = getTrendKey(record.getTestDate(), type);

            groupedData.computeIfAbsent(key, k -> new TrendAccumulator()).add(record.getNutrients());
        }

        Map<String, Map<String, Double>> trends = new LinkedHashMap<>();

        for (var entry : groupedData.entrySet()) {
            trends.put(entry.getKey(), entry.getValue().getAverages());
        }

        return new TrendResponse(trends);
    }

    private String getTrendKey(LocalDate date, TrendType type) {
        return switch (type) {
            case MONTHLY -> YearMonth.from(date).toString();
            case YEARLY -> String.valueOf(date.getYear());
            case SEASONAL -> getSeason(date);
        };
    }

    private String getSeason(LocalDate date) {
        int month = date.getMonthValue();

        String season =
                switch (month) {
                    case 12, 1, 2 -> "WINTER";
                    case 3, 4, 5 -> "SUMMER";
                    case 6, 7, 8, 9 -> "MONSOON";
                    case 10, 11 -> "POST_MONSOON";
                    default -> throw new IllegalStateException("Unexpected month: " + month);
                };

        return season + " " + date.getYear();
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

    private static class TrendAccumulator {

        private final Map<String, Double> sums = new HashMap<>();
        private int count;

        void add(NutrientData nutrients) {
            merge("ph", nutrients.ph());
            merge("ec", nutrients.ec());
            merge("organicCarbon", nutrients.organicCarbon());
            merge("nitrogen", nutrients.nitrogen());
            merge("phosphorus", nutrients.phosphorus());
            merge("potassium", nutrients.potassium());
            merge("sulfur", nutrients.sulfur());
            merge("zinc", nutrients.zinc());
            merge("boron", nutrients.boron());
            merge("iron", nutrients.iron());
            merge("copper", nutrients.copper());
            merge("manganese", nutrients.manganese());

            count++;
        }

        Map<String, Double> getAverages() {
            Map<String, Double> averages = new HashMap<>(sums);
            averages.replaceAll((k, v) -> v / count);
            return averages;
        }

        private void merge(String nutrient, double value) {
            sums.merge(nutrient, value, Double::sum);
        }
    }
}
