# EthGov deployment guide

## Local

1. Start infrastructure: `docker compose -f infrastructure/docker/docker-compose.yml up -d`
2. Ensure Postgres init scripts created per-service databases (`ethgov_*`).
3. Build: `./gradlew check`
4. Run a service: `./gradlew :services:ethgov-gateway:bootRun` (port 8100)

## Kubernetes

Use `infrastructure/helm/ethgov-service` with per-service values (image, DB URL, issuer, probes on `/actuator/health`). Namespace quotas live under `infrastructure/kubernetes/base`.

## Secrets

Inject DB credentials and OIDC issuer via environment / sealed secrets — never bake into images. See `secret-management.md`.
