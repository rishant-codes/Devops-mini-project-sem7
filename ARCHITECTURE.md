# Architecture — Air Quality Monitoring Platform

## Overview

```
┌─────────────┐      HTTP/JSON       ┌───────────────────┐
│   Frontend  │ ───────────────────> │      Backend       │
│  React+Vite │ <─────────────────── │  Spring Boot (17)  │
│  (port 5173 │       /api/*         │    (port 8080)     │
│  dev / 80   │                      │  in-memory store    │
│  in Docker) │                      └───────────────────┘
└─────────────┘
       ▲                                       ▲
       │ selenium tests drive the browser      │ unit tests (JUnit)
       │                                        │
┌──────────────────────────────────────────────────────────┐
│ selenium-tests (Maven module, JUnit 5 + Selenium WebDriver)│
└──────────────────────────────────────────────────────────┘

Jenkins pipeline: checkout → backend build/test → frontend build/lint →
start services → Selenium gate → docker build → (optional) docker push → deploy

Ansible: provisions a target VM (Java, Nginx, systemd service, firewall, versioned
jar with rollback symlink) and deploys the built jar + frontend bundle to it.
```

## Backend (`backend/`)

Spring Boot 3 / Java 17, single module, no database — data lives in memory
(`StationService`, backed by `ConcurrentHashMap`) so the whole stack runs with zero
external infra. This is a deliberate MVP trade-off (see `TROUBLESHOOTING.md` /
limitations).

- `model/` — `Station`, `Reading`, `AqiStatus` (thresholds: Good ≤50, Moderate ≤100,
  Unhealthy ≤150, Hazardous >150).
- `service/StationService.java` — all business logic: seeding, search, dashboard
  aggregation, alert filtering (AQI > 100), recording new readings.
- `web/` — REST controllers:
  - `GET /api/stations?q=` — searchable station list
  - `GET /api/stations/{id}` — drill-down (station + full reading history)
  - `POST /api/readings` — data/event entry
  - `GET /api/dashboard?q=` — summary indicators + filtered station list in one call
  - `GET /api/alerts` — stations currently above the AQI alert threshold
  - `GET /api/health` — liveness probe used by Docker/Ansible/Jenkins
- `config/WebConfig.java` — CORS for the Vite dev server origin.
- `web/ApiExceptionHandler.java` — maps unknown-station lookups to 404 and validation
  failures to 400 with a JSON `{"error": "..."}` body.

## Frontend (`frontend/`)

React 18 + Vite + Tailwind. `src/api.js` centralizes `fetch` calls against
`VITE_API_BASE_URL` (defaults to `http://localhost:8080`, overridden at Docker build
time). `App.jsx` composes:

- `StationTable` — searchable dashboard grid, click-through to drill-down.
- `AddReadingForm` — data/event entry, posts to `/api/readings` then refreshes.
- `AlertsPanel` — exception view for stations above threshold.
- `StationDrilldown` — modal showing a single station's reading history.

Every interactive element carries a `data-testid` so Selenium selectors don't depend on
CSS classes or text content.

## Testing (`selenium-tests/`)

Separate Maven module (kept out of the Spring Boot `backend` module so the app jar
never bundles test-only dependencies). 5 journeys covering dashboard load, search,
data entry, drill-down and alerts — see `selenium-tests/TEST_PLAN.md`.

## CI/CD (`Jenkinsfile`)

Single declarative pipeline, parameterized by target `ENVIRONMENT`
(staging/production), with a hard gate: Selenium failures `error()` out of the stage
before any Docker build/push/deploy stage can run. See `JENKINS.md` for setup and
`CONTINUOUS_TESTING.md` for the fail→fix→rerun demonstration.

## Containers (`backend/Dockerfile`, `frontend/Dockerfile`, `docker-compose.yml`)

Both images are multi-stage (build stage discarded, only the artifact — jar or static
`dist/` — ships in the runtime image). Backend runs as a non-root user. Frontend is
served by Nginx with SPA fallback routing. See `DOCKER.md`.

## Configuration management (`ansible/`)

Idempotent playbook provisioning a target node end-to-end (packages → user → app
directory → versioned jar + rollback symlink → systemd service → Nginx reverse proxy →
firewall → health check), plus a standalone `rollback.yml` for recovering a previous
version. See `ANSIBLE.md`.

## Why in-memory state instead of a database

The assignment's scope is the DevOps workflow (branching, CI, testing, containers,
provisioning) rather than data persistence. Swapping `StationService`'s
`ConcurrentHashMap` for a real repository (e.g. Spring Data JPA + Postgres) is the
natural "future enhancement" — see `TROUBLESHOOTING.md`.
