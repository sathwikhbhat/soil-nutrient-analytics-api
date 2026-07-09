package com.sathwikhbhat.soilanalytics.dashboard;

import com.sathwikhbhat.soilanalytics.dashboard.dto.*;
import com.sathwikhbhat.soilanalytics.exception.InvalidTrendTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewResponse> getDashboardOverview() {
        DashboardOverviewResponse response = dashboardService.getDashboardOverview();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nutrient-distribution")
    public ResponseEntity<NutrientDistributionResponse> getNutrientDistribution() {
        return ResponseEntity.ok(dashboardService.getNutrientDistribution());
    }

    @GetMapping("/deficiency-percentage")
    public ResponseEntity<DeficiencyPercentageResponse> getDeficiencyPercentage() {
        return ResponseEntity.ok(dashboardService.getDeficiencyPercentage());
    }

    @GetMapping("/average-nutrients")
    public ResponseEntity<AverageNutrientsResponse> getAverageNutrients() {
        return ResponseEntity.ok(dashboardService.getAverageNutrients());
    }

    @GetMapping("/trends")
    public ResponseEntity<TrendResponse> getTrends(@RequestParam String type) {
        TrendType trendType = parseTrendType(type);
        return ResponseEntity.ok(dashboardService.getTrends(trendType));
    }

    private TrendType parseTrendType(String type) {
        try {
            return TrendType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidTrendTypeException(type);
        }
    }
}
