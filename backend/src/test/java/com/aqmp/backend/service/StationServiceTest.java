package com.aqmp.backend.service;

import com.aqmp.backend.dto.DashboardSummary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StationServiceTest {

    @Test
    void seededStationsAreFourWithOneAlert() {
        StationService service = new StationService();

        DashboardSummary summary = service.getDashboardSummary(null);

        assertEquals(4, summary.stationsTotal());
        assertEquals(4, summary.stationsOnline());
        assertEquals(1, summary.activeAlerts());
    }

    @Test
    void searchFiltersStationsByNameOrLocation() {
        StationService service = new StationService();

        assertEquals(1, service.getStations("river").size());
        assertEquals(1, service.getStations("north").size());
        assertTrue(service.getStations("not-a-real-station").isEmpty());
    }

    @Test
    void newReadingAboveThresholdCreatesAlert() {
        StationService service = new StationService();

        service.recordReading(4L, 180, 90.0, 95.0);

        assertEquals(2, service.getAlerts().size());
    }
}
