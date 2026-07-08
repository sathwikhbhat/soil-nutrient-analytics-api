package com.sathwikhbhat.soilanalytics.dashboard;

import com.sathwikhbhat.soilanalytics.dashboard.dto.DashboardOverviewResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
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
}
