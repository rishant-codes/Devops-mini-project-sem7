# Air Quality Monitoring Platform — DevOps Mini Project

Spring Boot backend + React/Vite frontend, with the full DevOps toolchain (Git
branching, Jenkins CI/CD, Selenium testing, Docker, Ansible) built out as code.

## Repo layout

```
23102B0065-Mini-Proj/
├── backend/            Spring Boot 3 REST API (Java 17)
├── frontend/           React + Vite + Tailwind dashboard
├── selenium-tests/     Selenium WebDriver + JUnit 5 end-to-end suite
├── ansible/            Provisioning + deploy playbooks
├── Jenkinsfile         Pipeline as code
├── docker-compose.yml  Local multi-container run
├── ARCHITECTURE.md     How it all fits together
├── GIT_WORKFLOW.md     Branch/PR/merge/tag commands to run yourself
├── JENKINS.md          Install Jenkins + wire up this repo
├── CONTINUOUS_TESTING.md  Fail → fix → rerun demonstration
├── DOCKER.md           Build/run/inspect/stop the containers
├── ANSIBLE.md          Provision a target node + rollback demo
└── TROUBLESHOOTING.md  Common issues, limitations, future work
```

## Run it locally (no Docker/Jenkins needed)

```
# Terminal 1
cd backend
mvn spring-boot:run

# Terminal 2
cd frontend
npm install
npm run dev
```

Open http://localhost:5173. The dashboard, search, "Record a New Reading" form,
drill-down panel and alerts panel all talk to the backend on :8080.

## Run the Selenium suite

With both of the above still running:

```
cd selenium-tests
mvn test
```

See `selenium-tests/TEST_PLAN.md` for what each of the 5 journeys checks.

## What's implemented vs. what you need to verify yourself

Everything above was built and verified in this environment:
- Backend compiles (reviewed by hand — no local JDK/Maven was available in this
  sandbox to run `mvn`, so **you should run `mvn clean package` yourself first** to
  confirm a clean build before anything else).
- Frontend **was** installed and built here (`npm run build` and `npm run lint` both
  passed) against the real `package-lock.json`.
- Selenium test code compiles-by-inspection against the real `data-testid` attributes
  in the frontend, but was not executed here (no Chrome/Selenium runtime in this
  sandbox).
- Docker, Jenkins and Ansible have no daemon/install available in this sandbox, so
  their configs (Dockerfiles, Jenkinsfile, playbooks) are code-complete but
  unexecuted here — `DOCKER.md`, `JENKINS.md` and `ANSIBLE.md` each end with a
  "what working looks like" checklist so you can confirm them on your machine.

### Your first verification pass, in order

1. `cd backend && mvn clean package` → `BUILD SUCCESS`, then `mvn spring-boot:run` and
   hit http://localhost:8080/api/health.
2. `cd frontend && npm install && npm run dev`, open http://localhost:5173, click
   around (search, add a reading, drill into a station, check the alerts panel).
3. `cd selenium-tests && mvn test` with both servers still running from step 1/2.
4. Follow `DOCKER.md` end-to-end.
5. Follow `JENKINS.md` end-to-end, then `CONTINUOUS_TESTING.md` for the fail/fix demo.
6. Follow `ANSIBLE.md` end-to-end (a local VM/WSL target is fine).
7. Use `GIT_WORKFLOW.md` for the branching/PR/conflict/tag deliverables — nothing has
   been committed or pushed on your behalf.
