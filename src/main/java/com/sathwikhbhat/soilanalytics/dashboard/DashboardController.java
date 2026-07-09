package com.sathwikhbhat.soilanalytics.dashboard;

import com.sathwikhbhat.soilanalytics.dashboard.dto.AverageNutrientsResponse;
import com.sathwikhbhat.soilanalytics.dashboard.dto.DashboardOverviewResponse;
import com.sathwikhbhat.soilanalytics.dashboard.dto.DeficiencyPercentageResponse;
import com.sathwikhbhat.soilanalytics.dashboard.dto.NutrientDistributionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
