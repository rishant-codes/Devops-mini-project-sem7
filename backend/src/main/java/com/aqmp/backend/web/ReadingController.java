package com.aqmp.backend.web;

import com.aqmp.backend.dto.ReadingRequest;
import com.aqmp.backend.model.Reading;
import com.aqmp.backend.service.StationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadingController {

    private final StationService stationService;

    public ReadingController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/api/readings")
    @ResponseStatus(HttpStatus.CREATED)
    public Reading createReading(@Valid @RequestBody ReadingRequest request) {
        return stationService.recordReading(request.stationId(), request.aqi(), request.pm25(), request.pm10());
    }
}
