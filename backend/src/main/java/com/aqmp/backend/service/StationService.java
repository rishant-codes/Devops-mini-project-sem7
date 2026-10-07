package com.aqmp.backend.service;

import com.aqmp.backend.dto.DashboardSummary;
import com.aqmp.backend.dto.StationDetail;
import com.aqmp.backend.dto.StationView;
import com.aqmp.backend.model.AqiStatus;
import com.aqmp.backend.model.Reading;
import com.aqmp.backend.model.Station;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StationService {

    private static final int ALERT_THRESHOLD_AQI = 100;

    private final Map<Long, Station> stations = new ConcurrentHashMap<>();
    private final Map<Long, List<Reading>> readingsByStation = new ConcurrentHashMap<>();
    private final AtomicLong readingSequence = new AtomicLong(0);

    public StationService() {
        seed();
    }

    private void seed() {
        addStation(1L, "Downtown", "City Center");
        addStation(2L, "Riverside", "East Bank");
        addStation(3L, "Industrial Park", "North Zone");
        addStation(4L, "Hillcrest", "West Hills");

        recordReading(1L, 42, 10.1, 15.4, Instant.now().minus(2, ChronoUnit.HOURS));
        recordReading(2L, 78, 22.6, 30.2, Instant.now().minus(2, ChronoUnit.HOURS));
        recordReading(3L, 154, 60.3, 80.7, Instant.now().minus(1, ChronoUnit.HOURS));
        recordReading(4L, 31, 8.2, 12.1, Instant.now().minus(1, ChronoUnit.HOURS));
    }

    private void addStation(Long id, String name, String location) {
        stations.put(id, new Station(id, name, location));
        readingsByStation.put(id, new CopyOnWriteArrayList<>());
    }

    public List<StationView> getStations(String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase();
        return stations.values().stream()
                .filter(s -> normalized.isEmpty()
                        || s.getName().toLowerCase().contains(normalized)
                        || s.getLocation().toLowerCase().contains(normalized))
                .map(this::toStationView)
                .sorted(Comparator.comparing(StationView::name))
                .toList();
    }

    public StationDetail getStationDetail(Long id) {
        Station station = stations.get(id);
        if (station == null) {
            return null;
        }
        List<Reading> history = readingsByStation.getOrDefault(id, List.of()).stream()
                .sorted(Comparator.comparing(Reading::getRecordedAt).reversed())
                .toList();
        return new StationDetail(station.getId(), station.getName(), station.getLocation(), history);
    }

    public List<StationView> getAlerts() {
        return getStations(null).stream()
                .filter(s -> s.latestAqi() != null && s.latestAqi() > ALERT_THRESHOLD_AQI)
                .toList();
    }

    public DashboardSummary getDashboardSummary(String query) {
        List<StationView> allStations = getStations(null);
        List<StationView> filtered = getStations(query);

        long online = allStations.stream().filter(s -> s.latestAqi() != null).count();
        double avgAqi = allStations.stream()
                .filter(s -> s.latestAqi() != null)
                .mapToInt(StationView::latestAqi)
                .average()
                .orElse(0);
        int alerts = (int) allStations.stream()
                .filter(s -> s.latestAqi() != null && s.latestAqi() > ALERT_THRESHOLD_AQI)
                .count();

        return new DashboardSummary(
                (int) online,
                allStations.size(),
                Math.round(avgAqi * 10) / 10.0,
                alerts,
                filtered
        );
    }

    public Reading recordReading(Long stationId, int aqi, double pm25, double pm10) {
        return recordReading(stationId, aqi, pm25, pm10, Instant.now());
    }

    private Reading recordReading(Long stationId, int aqi, double pm25, double pm10, Instant recordedAt) {
        if (!stations.containsKey(stationId)) {
            throw new NoSuchElementException("Unknown station id: " + stationId);
        }
        Reading reading = new Reading(readingSequence.incrementAndGet(), stationId, aqi, pm25, pm10, recordedAt);
        readingsByStation.get(stationId).add(reading);
        return reading;
    }

    private StationView toStationView(Station station) {
        List<Reading> history = readingsByStation.getOrDefault(station.getId(), List.of());
        Reading latest = history.stream()
                .max(Comparator.comparing(Reading::getRecordedAt))
                .orElse(null);

        Integer latestAqi = latest == null ? null : latest.getAqi();
        AqiStatus status = latest == null ? null : latest.getStatus();
        Instant lastUpdated = latest == null ? null : latest.getRecordedAt();

        return new StationView(station.getId(), station.getName(), station.getLocation(), latestAqi, status, lastUpdated);
    }
}
