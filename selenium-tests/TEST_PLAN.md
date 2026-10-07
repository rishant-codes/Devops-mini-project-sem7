# Selenium Test Plan — Air Quality Monitoring Platform

## Preconditions

1. Backend running: `cd backend && mvn spring-boot:run` (http://localhost:8080)
2. Frontend running: `cd frontend && npm run dev` (http://localhost:5173)
3. Chrome installed locally (WebDriverManager downloads the matching driver automatically).

## Test data

The backend seeds four stations on startup, used as fixed test data for every journey:

| Station         | Seeded AQI | Expected status |
|------------------|-----------:|------------------|
| Downtown         | 42         | Good             |
| Riverside        | 78         | Moderate         |
| Industrial Park  | 154        | Unhealthy (alert)|
| Hillcrest        | 31         | Good             |

## Critical user journeys covered

| # | Journey | Test class | Key assertions |
|---|---------|-----------|-----------------|
| 1 | Dashboard loads with live station data | `DashboardLoadsTest` | 4 station rows render, no backend-error banner, no empty-state message |
| 2 | Searching the station list | `SearchStationsTest` | "river" narrows to 1 row (Riverside); an unknown query shows the empty state |
| 3 | Recording a new reading (data entry) | `AddReadingTest` | Submitting AQI 65 for Hillcrest updates that station's row without a page reload |
| 4 | Station drill-down | `StationDrilldownTest` | Clicking "Industrial Park" opens a panel showing its name and reading history; close button dismisses it |
| 5 | Alert / exception view | `AlertDisplayTest` | Industrial Park (AQI 154, above the 100 threshold) appears in the alerts panel |

## Execution

```
cd selenium-tests
mvn test
# or against a different environment:
mvn test -Dbase.url=http://myhost:5173 -Dapi.url=http://myhost:8080
```

Run headed (visible browser) for local debugging:

```
mvn test -Dheadless=false
```

## Failure reporting

- Every test extends `BaseTest`, which registers `ScreenshotOnFailureExtension`.
- On any assertion failure, a screenshot is saved to `selenium-tests/target/screenshots/<test-name>.png`.
- Surefire XML reports land in `selenium-tests/target/surefire-reports/`, which Jenkins publishes (see `../JENKINS.md`).

## Known limitation

Tests assume the seeded in-memory dataset (station IDs 1–4) and a backend restarted since the last test run would reset readings created by `AddReadingTest`. For CI, the pipeline always starts a fresh backend instance before running the suite.
