# Phase 10 Engineering Review — Construction Permits

**Project:** EthGov  
**Phase:** 10 — Construction Permits  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 11)  
**Prior phases:** Phase 0–9 APPROVED

## Independent review method

This review evaluates Phase 10 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-permits`

| Capability | Implementation |
|---|---|
| ConstructionPermit aggregate | Type, structure use, floors/area, cost, parcel-bound |
| Permit numbers | Tenant/year sequence → `PRM-YYYY-######` |
| Integrations by ID | Required `par_*`; optional `cas_*`, `wpi_*`, `doc_*`, `cit_*`/`org_*` |
| AuthZ | JWT; `permits.read` / `permits.write` |
| Events | Outbox → `ethgov.permits.events` |
| Schema | Flyway V1–V2; DB `ethgov_permits`; Compose init |
| Tests | Domain + ArchUnit + apply→review→issue→activate→complete integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; ArchUnit green |
| DDD | 9.4 | Parcel-required; completion as terminal success |
| Security | 9.3 | Scoped JWT; no public mutation |
| Performance | 9.2 | Unique tenant/permitNumber; parcel/org indexes |
| Maintainability | 9.3 | Consistent with licensing/land services |
| Operations | 9.2 | Port 8090, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear REST permit lifecycle |
| Testing | 9.3 | Domain + end-to-end green |
| Documentation | 9.3 | ADR 0016 + this review |
| Coverage (service line) | **77.8%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before Phase 11 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Permits service Jacoco line coverage 77.8% < 90%.** Cancel/revoke/expire/reject paths need additional tests before Phase 11 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Inspection scheduling / occupancy certificate deferred.
4. Fee assessment / payment proof deferred to Revenue/Payments.

### Low
1. Automated expiry scheduler not included (expire is an explicit command).
2. Multi-building campus permits deferred.

## Cross-phase consistency

- Links Land/Case/Workflow/Document/Organization by opaque IDs only
- Does not scaffold Revenue Platform / Phase 11+ services

## Approval gate

**APPROVED** — Phase 11 (Revenue Platform) started.
