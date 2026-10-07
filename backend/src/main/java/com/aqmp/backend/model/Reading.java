package com.aqmp.backend.model;

import java.time.Instant;

public class Reading {

    private final Long id;
    private final Long stationId;
    private final int aqi;
    private final double pm25;
    private final double pm10;
    private final Instant recordedAt;

    public Reading(Long id, Long stationId, int aqi, double pm25, double pm10, Instant recordedAt) {
        this.id = id;
        this.stationId = stationId;
        this.aqi = aqi;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.recordedAt = recordedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getStationId() {
        return stationId;
    }

    public int getAqi() {
        return aqi;
    }

    public double getPm25() {
        return pm25;
    }

    public double getPm10() {
        return pm10;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public AqiStatus getStatus() {
        return AqiStatus.fromAqi(aqi);
    }
}
