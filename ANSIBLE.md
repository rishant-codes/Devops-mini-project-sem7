# Ansible Provisioning Guide — Air Quality Monitoring Platform

Covers the "Configuration Management" and "Automated Provisioning and Reliability
Validation" deliverables. Target node is assumed to be Ubuntu (apt-based); adjust the
`apt` tasks in `ansible/playbook.yml` if yours is different.

## Server prerequisites this automates

| Prerequisite | How it's handled |
|---|---|
| Java 17 runtime | `openjdk-17-jre-headless` package |
| Web server | `nginx` package, serving the frontend build and reverse-proxying `/api/` |
| App user/group | dedicated `aqmp` system user, no login shell |
| App folder | `/opt/aqmp` owned by `aqmp` |
| Versioned jar + rollback point | `aqmp-backend-<version>.jar` + a `current.jar` symlink |
| Service management | `aqmp-backend.service` systemd unit, enabled + restart-on-failure |
| Ports | 80 (nginx) and 8080 (backend) opened via `ufw` |
| Health check | HTTP GET on `/api/health` after deploy |

## Prerequisites on your control machine

```
pip install ansible
```

Edit `ansible/inventory.ini` with your real target host, user and SSH key. For a quick
local test without a second machine, you can use WSL/a VM and:

```ini
[aqmp_servers]
aqmp-local ansible_host=127.0.0.1 ansible_connection=local ansible_user=<your-local-user>
```

(`ansible_connection=local` runs tasks on the control machine itself — fine for
demonstrating the playbook without a real second server, though in a real deployment
this should be a separate target node.)

## Build the artifacts the playbook deploys

```
cd 23102B0065-Mini-Proj/backend && mvn clean package -DskipTests
cd ../frontend && npm run build
```

Confirm `group_vars/all.yml`'s `backend_jar_src` / `frontend_dist_src` paths match what
got produced (the jar name includes the Maven version, e.g.
`backend-0.0.1-SNAPSHOT.jar`).

## 1. First run — provision + deploy

```
cd 23102B0065-Mini-Proj/ansible
ansible-playbook -i inventory.ini playbook.yml
```

## 2. Idempotency check (run it again, nothing should change)

```
ansible-playbook -i inventory.ini playbook.yml
```

Look at the play recap: on the second run every task should report `changed=0` (or only
the two "ensure running" tasks reporting `ok`, never `changed`) — that's your
idempotency evidence. Capture this output.

## 3. Health check

The playbook itself runs a health check task, but you can verify independently:

```
curl http://<target-host>/api/health
curl http://<target-host>/api/dashboard
```

## 4. Rollback / recovery demonstration

1. Bump `app_version` in `group_vars/all.yml` (e.g. `1.0.0` → `1.0.1`), rebuild the jar,
   rerun `playbook.yml` — this deploys the new version and `current.jar` now points at
   `aqmp-backend-1.0.1.jar`.
2. Simulate needing to recover the previous release:
   ```
   ansible-playbook -i inventory.ini rollback.yml --extra-vars "rollback_version=1.0.0"
   ```
3. Confirm the health check in the rollback output succeeds and `curl .../api/health`
   on the target responds normally again.

Capture the before/after `current.jar -> aqmp-backend-X.jar` symlink target
(`ls -l /opt/aqmp` on the target) as your rollback evidence.

## Puppet alternative

The assignment allows Puppet instead of Ansible. This repo ships the Ansible version
only, since it needs no agent/master setup on the target — if your course requires
Puppet specifically, the manifest would mirror the same tasks (package, user, file,
service resources) using a `site.pp` + module layout instead.
