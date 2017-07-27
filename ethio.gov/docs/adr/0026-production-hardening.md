# ADR 0026: Production Hardening / Gateway control plane

## Status

Accepted — Phase 20

## Context

Phases 1–19 delivered independently deployable domain services. Remaining non-functional work includes rate limiting keyed by Developer Platform subjects (`dap_*` / `ack_*`), circuit-breaker policy registries, immutable operational audit, and operational runbooks. A full L7 reverse-proxy data plane can sit outside the JVM; EthGov still needs a control plane for policies and audit.

## Decision

Implement `services/ethgov-gateway` as the Production Hardening control plane:

- Aggregate `RateLimitPolicy` — tenant-scoped `RLP-YYYY-######`, subject `APP|CREDENTIAL|TENANT|ROUTE`, windowed limits; DRAFT → ACTIVE ↔ SUSPENDED → RETIRED; DRAFT → CANCELLED; evaluate increments durable counters and returns 429 when exceeded
- Aggregate `ResiliencePolicy` — tenant-scoped `RSP-YYYY-######`, per-dependency failure threshold / wait / half-open settings for edge and service clients
- Aggregate `AuditEntry` — append-only `aud_*` ledger (SUCCESS|DENIED|FAILURE); rate-limit denials auto-append
- Database `ethgov_gateway`; outbox topic `ethgov.gateway.events`; port `8100`
- Scopes: `gateway.read`, `gateway.write`

Operational documentation under `docs/operations/` covers deployment, backup/DR, secrets, capacity, runbooks, and incident playbooks.

## Consequences

- Edge gateways call `POST /v1/rate-limits/evaluate` before forwarding partner traffic
- Redis-backed counters and live circuit-breaker libraries can replace DB counters later without changing the policy model
- Public developer portals and Fayda federation remain out of scope
