package com.aqmp.backend.web;

import com.aqmp.backend.dto.StationDetail;
import com.aqmp.backend.dto.StationView;
import com.aqmp.backend.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/api/stations")
    public List<StationView> stations(@RequestParam(name = "q", required = false) String query) {
        return stationService.getStations(query);
    }

    @GetMapping("/api/stations/{id}")
    public ResponseEntity<StationDetail> stationDetail(@PathVariable Long id) {
        StationDetail detail = stationService.getStationDetail(id);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }
}
