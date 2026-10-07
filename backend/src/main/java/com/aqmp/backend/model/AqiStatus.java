package com.aqmp.backend.model;

public enum AqiStatus {
    GOOD, MODERATE, UNHEALTHY, HAZARDOUS;

    public static AqiStatus fromAqi(int aqi) {
        if (aqi <= 50) return GOOD;
        if (aqi <= 100) return MODERATE;
        if (aqi <= 150) return UNHEALTHY;
        return HAZARDOUS;
    }
}
