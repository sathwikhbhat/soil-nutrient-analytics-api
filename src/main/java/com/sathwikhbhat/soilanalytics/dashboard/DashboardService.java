package com.sathwikhbhat.soilanalytics.dashboard;

import com.sathwikhbhat.soilanalytics.dashboard.dto.DashboardOverviewResponse;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public DashboardOverviewResponse getDashboardOverview() {
        return new DashboardOverviewResponse(
                dashboardRepository.getTotalSamples(),
                dashboardRepository.getDistrictCoverage(),
                dashboardRepository.getCropCoverage()
        );
    }
}
