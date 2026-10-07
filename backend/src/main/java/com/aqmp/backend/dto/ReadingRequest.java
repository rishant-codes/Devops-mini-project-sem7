package com.aqmp.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReadingRequest(
        @NotNull Long stationId,
        @Min(0) int aqi,
        @Min(0) double pm25,
        @Min(0) double pm10
) {
}
