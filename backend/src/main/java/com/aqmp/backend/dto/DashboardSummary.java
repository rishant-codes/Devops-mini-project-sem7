package com.aqmp.backend.dto;

import java.util.List;

public record DashboardSummary(
        int stationsOnline,
        int stationsTotal,
        double averageAqi,
        int activeAlerts,
        List<StationView> stations
) {
}
