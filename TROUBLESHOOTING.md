# Troubleshooting, Limitations & Future Enhancements

## Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| Frontend shows "Could not reach the backend" | Backend not running, or CORS origin mismatch | Start backend on 8080; confirm `WebConfig.java` allows your frontend's actual origin |
| `mvn` / `java` not found | JDK 17 + Maven not installed or not on `PATH` | Install Temurin 17 and Maven 3.9+; verify with `java -version`, `mvn -v` |
| Selenium tests can't find Chrome | Chrome not installed, or WebDriverManager has no internet access to fetch the matching driver | Install Chrome; WebDriverManager needs outbound internet the first time it resolves a driver version |
| Selenium tests fail with stale element / timing errors | App not fully loaded before the test interacts with it | Tests already `wait.until(...)` on `data-testid` elements; increase the `Duration.ofSeconds(10)` in `BaseTest` if your machine is slow |
| `docker build` fails on `npm ci` | `package-lock.json` out of sync with `package.json` | Run `npm install` locally first to regenerate the lock file, then rebuild |
| Jenkins stage "Start Services for E2E" health check loop times out | Backend failed to start (port already in use, or build artifact missing) | Check `backend/backend.log` the stage created; confirm port 8080 is free on the Jenkins agent |
| Ansible `apt` task fails | Target isn't Debian/Ubuntu | Swap the `apt` module for `yum`/`dnf` and adjust package names (`java-17-openjdk-headless`, etc.) |
| Ansible health check task fails after deploy | systemd service crashed on start | SSH to the target, `journalctl -u aqmp-backend -n 50` |

## Known limitations

- **No persistence.** All stations/readings live in memory (`ConcurrentHashMap`); a
  backend restart resets to the 4 seeded stations. Acceptable for this assignment's
  scope (the DevOps workflow, not data durability).
- **Single AQI metric drives status.** Real AQI calculation blends multiple pollutants
  with EPA breakpoint tables; this MVP uses a simplified single-value threshold model.
- **No authentication.** Every endpoint is open — fine for a local/demo deployment,
  not for a real multi-tenant product.
- **Selenium suite assumes seeded IDs 1–4.** Running it twice against the same
  long-lived backend process will have `AddReadingTest`'s extra reading accumulate
  (harmless, since assertions don't check history length), but a database with
  auto-increment IDs starting elsewhere would need the tests' station IDs updated.
- **Docker/Jenkins/Ansible are provided as code only** in this environment (no
  Docker daemon, Jenkins, or Ansible control node was available to execute them here)
  — see `DOCKER.md` / `JENKINS.md` / `ANSIBLE.md` for exactly how to verify each one
  yourself, and what a successful run looks like.

## Future enhancements

- Replace the in-memory store with Spring Data JPA + PostgreSQL; add a Flyway/Liquibase
  migration and an Ansible/Docker-Compose Postgres service.
- Proper EPA AQI breakpoint calculation from raw PM2.5/PM10/O3/CO readings instead of a
  single supplied AQI value.
- WebSocket or SSE push for live dashboard updates instead of client-side refresh.
- Role-based auth (operator vs. viewer) before exposing `/api/readings` publicly.
- Blue/green or canary deploy stage in the Jenkinsfile instead of a plain
  stop/replace `docker run`.
- Helm chart / Kubernetes manifests if the platform needs to scale beyond a single
  Docker host.
