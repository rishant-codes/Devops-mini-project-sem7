# Docker Guide — Air Quality Monitoring Platform

You said you have Docker installed but want to verify it yourself. Everything below is
ready to run as-is — this file is the checklist. Run these from
`23102B0065-Mini-Proj/`.

## 0. Sanity check Docker itself

```
docker --version
docker info          # confirms the Docker daemon is actually running
```

If `docker info` errors out, start Docker Desktop (Windows/Mac) or the `docker` service
(Linux) first.

## 1. Build the images

```
docker build -t aqmp-backend:1.0 ./backend
docker build -t aqmp-frontend:1.0 --build-arg VITE_API_BASE_URL=http://localhost:8080 ./frontend
```

Check the images and tags exist:

```
docker images | grep aqmp
```

## 2. Run the containers (manually, to see the full lifecycle)

```
docker run -d --name aqmp-backend -p 8080:8080 aqmp-backend:1.0
docker run -d --name aqmp-frontend -p 8081:80 aqmp-frontend:1.0
```

Verify:

```
docker ps                                   # both containers "Up"
curl http://localhost:8080/api/health       # {"status":"UP",...}
curl http://localhost:8080/api/dashboard    # seeded station JSON
```

Open http://localhost:8081 in a browser — you should see the dashboard talking to the
backend container.

## 3. Inspect logs

```
docker logs aqmp-backend
docker logs -f aqmp-frontend     # -f to follow/tail
```

## 4. Stop / restart / remove (full lifecycle)

```
docker stop aqmp-backend aqmp-frontend
docker start aqmp-backend aqmp-frontend
docker restart aqmp-backend

docker stop aqmp-backend aqmp-frontend
docker rm aqmp-backend aqmp-frontend
docker rmi aqmp-backend:1.0 aqmp-frontend:1.0
```

## 5. Or do all of the above with Compose (recommended)

```
docker compose up -d --build     # build + run both containers
docker compose ps
docker compose logs -f backend
docker compose down              # stop + remove containers (keeps images)
docker compose down --rmi local  # also remove the images built by compose
```

## What "working" looks like

- `docker compose ps` shows both services as `running`/`healthy`.
- `GET http://localhost:8080/api/dashboard` returns the 4 seeded stations.
- `http://localhost:8081` renders the dashboard, and the search/add-reading/alerts
  features work against the containerized backend exactly as they do when run locally
  with `mvn spring-boot:run` + `npm run dev`.

If port 8081 or 8080 is already taken on your machine, change the left-hand side of the
`ports:` mapping in `docker-compose.yml` (e.g. `"8090:80"`).
