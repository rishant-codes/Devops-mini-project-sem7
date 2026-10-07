# Jenkins Guide — Air Quality Monitoring Platform

You don't have Jenkins installed yet. This is the checklist to install it, wire up this
repo, and confirm the pipeline actually works — do this yourself and use the "what
success looks like" section at the bottom to verify.

## 1. Install Jenkins

**Easiest path (Docker, since you have Docker):**

```
docker run -d --name jenkins -p 8090:8080 -p 50000:50000 ^
  -v jenkins_home:/var/jenkins_home ^
  -v //var/run/docker.sock:/var/run/docker.sock ^
  jenkins/jenkins:lts
```

(Use `-p 8090:8080` instead of `8080:8080` so it doesn't clash with the backend's own
port 8080.)

Get the initial admin password:

```
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Open http://localhost:8090, paste the password, choose **Install suggested plugins**.

**Alternative (native Windows installer):** download from https://www.jenkins.io/download/,
run the `.msi`, it installs as a Windows service on port 8080 — change the port during
setup if it conflicts with the backend.

## 2. Install the plugins this pipeline needs

Manage Jenkins → Plugins → Available, install:
- Git
- Pipeline (usually already bundled)
- Maven Integration
- NodeJS
- JUnit
- Docker Pipeline
- Credentials Binding

## 3. Configure tools (Manage Jenkins → Tools)

- **Maven**: add an installation named exactly `Maven3`.
- **NodeJS**: add an installation named exactly `Node20` (NodeJS 20.x).

These names match the `tools {}` block in the `Jenkinsfile` — if you name them
differently, update the Jenkinsfile to match.

## 4. (Optional) Add Docker Hub credentials

Only needed if you set the `PUSH_IMAGE` parameter to `true`.

Manage Jenkins → Credentials → System → Global credentials → Add Credentials:
- Kind: **Username with password**
- ID: `dockerhub-creds` (must match the Jenkinsfile)
- Username/password: your Docker Hub account

## 5. Create the job

1. New Item → **Pipeline** → name it `aqmp-pipeline`.
2. Pipeline → Definition: **Pipeline script from SCM**.
3. SCM: **Git**, Repository URL: `https://github.com/rishant-codes/DEVOPS-mini-project-23102B0065.git`
4. Branch: `*/main` (or `*/development`, whichever you build from).
5. Script Path: `23102B0065-Mini-Proj/Jenkinsfile`
6. Save.

## 6. Trigger: commit vs. scheduled polling

The Jenkinsfile already has `pollSCM('H/5 * * * *')`, so Jenkins checks the repo every
5 minutes with no extra setup — good enough for local demo purposes.

To trigger on every commit instead (requires Jenkins to be reachable from GitHub, e.g.
via ngrok if running locally):
1. GitHub repo → Settings → Webhooks → Add webhook → Payload URL
   `http://<your-jenkins-host>/github-webhook/`, content type `application/json`,
   event: "Just the push event".
2. In the Jenkins job → Configure → Build Triggers → check **GitHub hook trigger for
   GITScm polling**.

## 7. Run it

Build with Parameters → set `ENVIRONMENT=staging`, `RUN_SELENIUM=true`,
`PUSH_IMAGE=false` → Build.

## What "working" looks like

- **Build job**: the job's console log shows `BUILD SUCCESS` for both the backend
  Maven build and the frontend `npm run build`, and the backend `.jar` appears under
  the job's **Build Artifacts**.
- **Trigger evidence**: either the job fires within ~5 minutes of a `git push` (polling)
  or immediately (webhook) — check **Build History** timestamps against your commit
  time.
- **Continuous testing**: the job has a **Test Result** trend graph (JUnit plugin) once
  Selenium tests have run at least once, and a failing test (try temporarily breaking
  `data-testid='search-input'` in `App.jsx`) turns the build **red** and stops before
  the Docker/Deploy stages run (see `CONTINUOUS_TESTING.md`).
- **Docker stages**: `docker images` on the Jenkins host/container shows
  `aqmp-backend:<build-number>` and `aqmp-frontend:<build-number>` after a successful
  build.
- **Deploy stage**: `docker ps` shows `aqmp-backend-staging` / `aqmp-frontend-staging`
  (or `-production`) running after the pipeline completes.
