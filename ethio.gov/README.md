# EthGov

Production-grade, enterprise digital government platform for Ethiopia.

## Phase status

- **Phase 0 — Foundation Platform:** APPROVED
- **Phase 1 — Identity Platform:** APPROVED
- **Phase 2 — Citizen Registry:** APPROVED
- **Phase 3 — Organization Platform:** APPROVED
- **Phase 4 — Government Directory:** APPROVED
- **Phase 5 — Document Platform:** APPROVED
- **Phase 6 — Workflow Engine:** APPROVED
- **Phase 7 — Case Management:** APPROVED
- **Phase 8 — Land Registry:** APPROVED
- **Phase 9 — Business Licensing:** APPROVED
- **Phase 10 — Construction Permits:** APPROVED
- **Phase 11 — Revenue Platform:** APPROVED
- **Phase 12 — Payments:** APPROVED
- **Phase 13 — Notification Platform:** APPROVED
- **Phase 14 — GIS:** APPROVED
- **Phase 15 — Reporting:** APPROVED
- **Phase 16 — Search:** APPROVED
- **Phase 17 — Open Data:** APPROVED
- **Phase 18 — Analytics:** APPROVED
- **Phase 19 — Developer Platform:** APPROVED
- **Phase 20 — Production Hardening:** APPROVED

## Mission

Build a cloud-native, event-driven, multi-tenant backend that enables municipalities, regional governments, and federal institutions to digitize public services.

This repository is backend-only. No frontend.

## Phase 0 scope

Phase 0 delivers shared platform foundations used by every future service:

| Module | Purpose |
|---|---|
| `platform/ethgov-bom` | Aligned dependency versions (Spring Boot 3.4, CloudEvents, OTel, Testcontainers) |
| `platform/ethgov-platform-commons` | Tenant IDs, ETB money, Ethiopian phone/address/calendar, i18n, pagination, domain errors |
| `platform/ethgov-platform-web` | RFC7807 problem details, correlation/tenant request filter |
| `platform/ethgov-platform-persistence` | Tenant entity base, optimistic locking, outbox/inbox entities |
| `platform/ethgov-platform-messaging` | CloudEvents factory, transactional outbox publisher, inbox guard |
| `platform/ethgov-platform-security` | Principal model, JWT claim mapping, ABAC helper |
| `platform/ethgov-platform-observability` | Span enrichment + structured logging fields |
| `services/ethgov-identity` | OAuth2/OIDC Identity Platform (Phase 1) |
| `services/ethgov-citizen` | Citizen Registry (Phase 2) |
| `services/ethgov-organization` | Organization Platform (Phase 3) |
| `services/ethgov-directory` | Government Directory (Phase 4) |
| `services/ethgov-document` | Document Platform (Phase 5) |
| `services/ethgov-workflow` | Workflow Engine (Phase 6) |
| `services/ethgov-case` | Case Management (Phase 7) |
| `services/ethgov-land` | Land Registry (Phase 8) |
| `services/ethgov-licensing` | Business Licensing (Phase 9) |
| `services/ethgov-permits` | Construction Permits (Phase 10) |
| `services/ethgov-revenue` | Revenue Platform (Phase 11) |
| `services/ethgov-payments` | Payments (Phase 12) |
| `services/ethgov-notification` | Notification Platform (Phase 13) |
| `services/ethgov-gis` | GIS (Phase 14) |
| `services/ethgov-reporting` | Reporting (Phase 15) |
| `services/ethgov-search` | Search (Phase 16) |
| `services/ethgov-opendata` | Open Data (Phase 17) |
| `services/ethgov-analytics` | Analytics (Phase 18) |
| `services/ethgov-developer` | Developer Platform (Phase 19) |
| `services/ethgov-gateway` | Production Hardening / Gateway control plane (Phase 20) |

Infrastructure baselines: Docker Compose (PostGIS, Redis, Kafka, Jaeger, Prometheus, Grafana), Helm chart, Kubernetes namespace quotas, Terraform module contracts.

**Not in Phase 0:** deployable business microservices, Identity Platform, or any domain service scaffolding.

## Prerequisites

- JDK 21
- Docker (for local dependencies / Testcontainers)
- Gradle Wrapper (generated after first bootstrap)

## Build

```bash
./gradlew check
```

## Local dependencies

```bash
docker compose -f infrastructure/docker/docker-compose.yml up -d
```

Default timezone for local containers and JVM tests: `Africa/Addis_Ababa`.

## Engineering rules

1. One bounded context at a time.
2. No empty microservice scaffolding.
3. No placeholder / TODO production code.
4. Each phase ends with an independent engineering review and explicit approval before the next phase starts.

## Documentation

- Architecture: `docs/architecture/`
- ADRs: `docs/adr/`
- Onboarding: `docs/onboarding/README.md`
- Phase reviews: `docs/reviews/`
