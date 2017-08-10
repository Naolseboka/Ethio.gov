# Phase 20 Engineering Review — Production Hardening

**Project:** EthGov  
**Phase:** 20 — Production Hardening  
**Review date:** 2026-07-25  
**Status:** APPROVED  
**Prior phases:** Phase 0–19 APPROVED

## Independent review method

This review evaluates Phase 20 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, ops docs, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-gateway`

| Capability | Implementation |
|---|---|
| RateLimitPolicy aggregate | APP/CREDENTIAL/TENANT/ROUTE subjects; windowed evaluate → 429 |
| ResiliencePolicy aggregate | Per-dependency circuit settings; activate/suspend/retire |
| AuditEntry ledger | Append-only; denials auto-recorded |
| Numbers | `RLP-YYYY-######` / `RSP-YYYY-######` |
| AuthZ | JWT; `gateway.read` / `gateway.write` |
| Events | Outbox → `ethgov.gateway.events` |
| Schema | Flyway V1–V2; DB `ethgov_gateway`; Compose init |
| Ops docs | Deployment, backup/DR, secrets, capacity, runbooks, playbooks |
| Tests | Domain + ArchUnit + evaluate/deny + resilience/audit |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.5 | Hexagonal control plane; edge evaluate API |
| DDD | 9.4 | Clear aggregates; immutable audit |
| Security | 9.3 | Scope-gated; opaque subject keys; no secret storage |
| Performance | 8.8 | DB counters adequate for control plane; Redis deferred |
| Maintainability | 9.4 | Matches prior service patterns |
| Operations | 9.5 | Runbooks / DR / capacity documented |
| Developer Experience | 9.3 | Consistent REST + problem details |
| Testing | 9.2 | Domain + ArchUnit + Testcontainers |
| Documentation | 9.5 | ADR 0026 + ops package |
| Coverage (service line) | **85.7%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Gateway service Jacoco line coverage 85.7% < 90%.** Suspend/retire/cancel edge paths and CREDENTIAL subject evaluate before freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Rate counters are Postgres-backed; Redis recommended before multi-k RPS evaluate traffic.
4. Live L7 reverse-proxy data plane / API-key hash validation at the edge remain external.

### Low
1. Public developer portal still deferred.
2. Fayda federation still deferred.

## Cross-phase consistency

- Rate-limit subjects accept Developer Platform `dap_*` / `ack_*` IDs
- Does not introduce frontend or unrelated domain scaffolding

## Approval gate

**APPROVED** — Phase 20 complete. EthGov phased delivery (0–20) is finished.
