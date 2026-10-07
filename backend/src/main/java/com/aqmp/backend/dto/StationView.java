package com.aqmp.backend.dto;

import com.aqmp.backend.model.AqiStatus;

import java.time.Instant;

public record StationView(
        Long id,
        String name,
        String location,
        Integer latestAqi,
        AqiStatus status,
        Instant lastUpdated
) {
}
