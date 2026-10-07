package com.aqmp.backend.web;

import com.aqmp.backend.dto.DashboardSummary;
import com.aqmp.backend.service.StationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private final StationService stationService;

    public DashboardController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/api/dashboard")
    public DashboardSummary dashboard(@RequestParam(name = "q", required = false) String query) {
        return stationService.getDashboardSummary(query);
    }
}
