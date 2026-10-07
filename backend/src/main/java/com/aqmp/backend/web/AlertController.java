package com.aqmp.backend.web;

import com.aqmp.backend.dto.StationView;
import com.aqmp.backend.service.StationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlertController {

    private final StationService stationService;

    public AlertController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/api/alerts")
    public List<StationView> alerts() {
        return stationService.getAlerts();
    }
}
