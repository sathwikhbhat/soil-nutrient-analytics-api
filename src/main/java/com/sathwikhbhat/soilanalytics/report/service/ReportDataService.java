package com.sathwikhbhat.soilanalytics.report.service;

import com.sathwikhbhat.soilanalytics.classification.ClassificationService;
import com.sathwikhbhat.soilanalytics.classification.NutrientLevel;
import com.sathwikhbhat.soilanalytics.classification.dto.NutrientClassificationResponse;
import com.sathwikhbhat.soilanalytics.dashboard.DashboardService;
import com.sathwikhbhat.soilanalytics.dashboard.dto.AverageNutrientsResponse;
import com.sathwikhbhat.soilanalytics.dashboard.dto.DashboardOverviewResponse;
import com.sathwikhbhat.soilanalytics.dashboard.dto.DeficiencyPercentageResponse;
import com.sathwikhbhat.soilanalytics.dashboard.dto.NutrientDistributionResponse;
import com.sathwikhbhat.soilanalytics.entity.NutrientData;
import com.sathwikhbhat.soilanalytics.entity.SoilRecord;
import com.sathwikhbhat.soilanalytics.report.dto.ReportData;
import com.sathwikhbhat.soilanalytics.report.dto.ReportSection;
import com.sathwikhbhat.soilanalytics.report.enums.ReportType;
import com.sathwikhbhat.soilanalytics.report.util.NutrientFields;
import com.sathwikhbhat.soilanalytics.repository.SoilRecordRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReportDataService {

    private final DashboardService dashboardService;
    private final SoilRecordRepository soilRecordRepository;
    private final ClassificationService classificationService;

    @Value("${app.timezone}")
    private String timezone;

    public ReportDataService(
            DashboardService dashboardService,
            SoilRecordRepository soilRecordRepository,
            ClassificationService classificationService) {
        this.dashboardService = dashboardService;
        this.soilRecordRepository = soilRecordRepository;
        this.classificationService = classificationService;
    }

    public ReportData getReportData(ReportType type) {
        return switch (type) {
            case NUTRIENT_STATUS -> buildNutrientStatusReport();
            case SOIL_FERTILITY -> buildSoilFertilityReport();
            case CROP_WISE -> buildCropWiseReport();
            case DISTRICT_SUMMARY -> buildDistrictSummaryReport();
        };
    }

    private ReportData buildNutrientStatusReport() {
        DashboardOverviewResponse overview = dashboardService.getDashboardOverview();
        AverageNutrientsResponse averages = dashboardService.getAverageNutrients();
        NutrientDistributionResponse distribution = dashboardService.getNutrientDistribution();
        DeficiencyPercentageResponse deficiency = dashboardService.getDeficiencyPercentage();

        List<ReportSection> sections = List.of(
                new ReportSection(
                        "Overview",
                        List.of("Metric", "Value"),
                        List.of(
                                List.of("Total Samples", String.valueOf(overview.totalSamples())),
                                List.of("District Coverage", String.valueOf(overview.districtCoverage())),
                                List.of("Crop Coverage", String.valueOf(overview.cropCoverage())))),
                buildAverageNutrientsSection(
                        "Average Nutrients", averages.averageNutrients(), NutrientFields.ALL_NUTRIENTS),
                buildDistributionSection(
                        "Nutrient Distribution", distribution.distribution(), NutrientFields.ALL_NUTRIENTS),
                buildDeficiencySection(
                        "Deficiency Percentage", deficiency.deficiencyPercentage(), NutrientFields.ALL_NUTRIENTS));

        return createReportData("Nutrient Status Report", ReportType.NUTRIENT_STATUS, sections);
    }

    private ReportData buildSoilFertilityReport() {
        AverageNutrientsResponse averages = dashboardService.getAverageNutrients();
        NutrientDistributionResponse distribution = dashboardService.getNutrientDistribution();
        DeficiencyPercentageResponse deficiency = dashboardService.getDeficiencyPercentage();

        List<SoilRecord> records = soilRecordRepository.findAll();
        long lowFertilitySamples = records.stream()
                .filter(record ->
                        countLowFertilityIndicators(classificationService.classify(record.getNutrients())) >= 3)
                .count();

        double lowFertilityPercentage = records.isEmpty() ? 0.0 : (lowFertilitySamples * 100.0) / records.size();

        Map<String, Map<NutrientLevel, Integer>> fertilityDistribution = retainMetrics(distribution.distribution());
        Map<String, Double> fertilityAverages = retainMetrics(averages.averageNutrients());
        Map<String, Double> fertilityDeficiency = retainMetrics(deficiency.deficiencyPercentage());

        List<ReportSection> sections = List.of(
                new ReportSection(
                        "Fertility Summary",
                        List.of("Metric", "Value"),
                        List.of(
                                List.of("Total Samples", String.valueOf(records.size())),
                                List.of("Low Fertility Samples", String.valueOf(lowFertilitySamples)),
                                List.of("Low Fertility Percentage", formatPercentage(lowFertilityPercentage)))),
                buildAverageNutrientsSection(
                        "Fertility Indicator Averages", fertilityAverages, NutrientFields.FERTILITY_NUTRIENTS),
                buildDistributionSection(
                        "Fertility Indicator Distribution", fertilityDistribution, NutrientFields.FERTILITY_NUTRIENTS),
                buildDeficiencySection(
                        "Fertility Indicator Deficiency", fertilityDeficiency, NutrientFields.FERTILITY_NUTRIENTS));

        return createReportData("Soil Fertility Report", ReportType.SOIL_FERTILITY, sections);
    }

    private ReportData buildCropWiseReport() {
        List<SoilRecord> records = soilRecordRepository.findAll();
        return buildGroupedSummaryReport(
                "Crop-wise Report",
                ReportType.CROP_WISE,
                "Crop-wise Nutrient Summary",
                "Crop",
                records,
                SoilRecord::getCrop);
    }

    private ReportData buildDistrictSummaryReport() {
        List<SoilRecord> records = soilRecordRepository.findAll();
        return buildGroupedSummaryReport(
                "District Summary Report",
                ReportType.DISTRICT_SUMMARY,
                "District Nutrient Summary",
                "District",
                records,
                record -> record.getLocation().district());
    }

    private ReportSection buildAverageNutrientsSection(
            String title, Map<String, Double> averages, List<String> nutrients) {
        List<List<String>> rows = new ArrayList<>();

        for (String nutrient : nutrients) {
            rows.add(List.of(nutrient, formatDecimal(averages.getOrDefault(nutrient, 0.0))));
        }

        return new ReportSection(title, List.of("Nutrient", "Average"), rows);
    }

    private ReportSection buildDistributionSection(
            String title, Map<String, Map<NutrientLevel, Integer>> distribution, List<String> nutrients) {
        List<List<String>> rows = new ArrayList<>();

        for (String nutrient : nutrients) {
            Map<NutrientLevel, Integer> levels = distribution.getOrDefault(nutrient, Map.of());
            rows.add(List.of(
                    nutrient,
                    String.valueOf(levels.getOrDefault(NutrientLevel.LOW, 0)),
                    String.valueOf(levels.getOrDefault(NutrientLevel.MEDIUM, 0)),
                    String.valueOf(levels.getOrDefault(NutrientLevel.HIGH, 0))));
        }

        return new ReportSection(title, List.of("Nutrient", "Low", "Medium", "High"), rows);
    }

    private ReportSection buildDeficiencySection(
            String title, Map<String, Double> deficiencyPercentage, List<String> nutrients) {
        List<List<String>> rows = new ArrayList<>();

        for (String nutrient : nutrients) {
            rows.add(List.of(nutrient, formatPercentage(deficiencyPercentage.getOrDefault(nutrient, 0.0))));
        }

        return new ReportSection(title, List.of("Nutrient", "Deficiency %"), rows);
    }

    private Map<String, Double> calculateAverageNutrients(List<SoilRecord> records) {
        if (records.isEmpty()) {
            return Map.of();
        }

        Map<String, Double> sums = new HashMap<>();

        for (SoilRecord record : records) {
            NutrientData nutrients = record.getNutrients();
            for (String nutrient : NutrientFields.ALL_NUTRIENTS) {
                sums.merge(nutrient, NutrientFields.getValue(nutrients, nutrient), Double::sum);
            }
        }

        Map<String, Double> averages = new LinkedHashMap<>();
        sums.forEach((nutrient, sum) -> averages.put(nutrient, sum / records.size()));
        return averages;
    }

    private Map<String, Double> calculateDeficiencyPercentage(List<SoilRecord> records) {
        if (records.isEmpty()) {
            return Map.of();
        }

        Map<String, Map<NutrientLevel, Integer>> distribution = new HashMap<>();

        for (SoilRecord record : records) {
            NutrientClassificationResponse classification = classificationService.classify(record.getNutrients());
            for (String nutrient : NutrientFields.ALL_NUTRIENTS) {
                distribution
                        .computeIfAbsent(nutrient, key -> new EnumMap<>(NutrientLevel.class))
                        .merge(NutrientFields.getLevel(classification, nutrient), 1, Integer::sum);
            }
        }

        Map<String, Double> deficiencyPercentage = new LinkedHashMap<>();

        for (String nutrient : NutrientFields.ALL_NUTRIENTS) {
            int lowCount = distribution.getOrDefault(nutrient, Map.of()).getOrDefault(NutrientLevel.LOW, 0);
            deficiencyPercentage.put(nutrient, (lowCount * 100.0) / records.size());
        }

        return deficiencyPercentage;
    }

    private int countLowFertilityIndicators(NutrientClassificationResponse classification) {
        int lowCount = 0;

        for (String nutrient : NutrientFields.FERTILITY_NUTRIENTS) {
            if (NutrientFields.getLevel(classification, nutrient) == NutrientLevel.LOW) {
                lowCount++;
            }
        }

        return lowCount;
    }

    private ReportData buildGroupedSummaryReport(
            String reportTitle,
            ReportType reportType,
            String sectionTitle,
            String groupLabel,
            List<SoilRecord> records,
            Function<SoilRecord, String> groupKeyExtractor) {
        Map<String, List<SoilRecord>> recordsByGroup =
                records.stream().collect(Collectors.groupingBy(groupKeyExtractor, TreeMap::new, Collectors.toList()));

        List<List<String>> rows = new ArrayList<>();

        for (var entry : recordsByGroup.entrySet()) {
            String groupName = entry.getKey();
            List<SoilRecord> groupRecords = entry.getValue();
            Map<String, Double> averages = calculateAverageNutrients(groupRecords);
            Map<String, Double> deficiency = calculateDeficiencyPercentage(groupRecords);

            List<String> row = new ArrayList<>();
            row.add(groupName);
            row.add(String.valueOf(groupRecords.size()));
            for (String nutrient : NutrientFields.FERTILITY_NUTRIENTS) {
                row.add(formatDecimal(averages.getOrDefault(nutrient, 0.0)));
                row.add(formatPercentage(deficiency.getOrDefault(nutrient, 0.0)));
            }
            rows.add(row);
        }

        List<String> headers = new ArrayList<>(List.of(groupLabel, "Sample Count"));
        for (String nutrient : NutrientFields.FERTILITY_NUTRIENTS) {
            headers.add(nutrient + " (avg)");
            headers.add(nutrient + " deficiency %");
        }

        List<ReportSection> sections = List.of(new ReportSection(sectionTitle, headers, rows));

        return createReportData(reportTitle, reportType, sections);
    }

    private <T> Map<String, T> retainMetrics(Map<String, T> values) {
        return NutrientFields.FERTILITY_NUTRIENTS.stream()
                .filter(values::containsKey)
                .collect(Collectors.toMap(Function.identity(), values::get, (a, b) -> a, LinkedHashMap::new));
    }

    private ReportData createReportData(String title, ReportType type, List<ReportSection> sections) {
        return new ReportData(title, type, LocalDateTime.now(ZoneId.of(timezone)), sections);
    }

    private String formatDecimal(double value) {
        return String.format("%.2f", value);
    }

    private String formatPercentage(double value) {
        return String.format("%.2f%%", value);
    }
}
