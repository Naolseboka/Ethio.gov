# Phase 8 Engineering Review — Land Registry

**Project:** EthGov  
**Phase:** 8 — Land Registry  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 9)  
**Prior phases:** Phase 0–7 APPROVED

## Independent review method

This review evaluates Phase 8 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-land`

| Capability | Implementation |
|---|---|
| LandParcel aggregate | Address, use, tenure, ownership, encumbrance, docs/case links |
| Parcel numbers | Tenant/year sequence → `LND-YYYY-######` |
| Integrations by ID | Optional `cas_*` case, `doc_*` documents, `cit_*` / `org_*` owners |
| AuthZ | JWT; `land.read` / `land.write` |
| Events | Outbox → `ethgov.land.events` |
| Schema | Flyway V1–V2; DB `ethgov_land`; Compose init |
| Tests | Domain + ArchUnit + register→transfer→encumber→retire integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; ArchUnit green |
| DDD | 9.4 | Explicit tenure/ownership invariants; terminal immutability |
| Security | 9.3 | Scoped JWT; no public mutation |
| Performance | 9.2 | Unique tenant/parcelNumber; owner/case indexes |
| Maintainability | 9.3 | Consistent with prior services |
| Operations | 9.2 | Port 8088, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear REST parcel lifecycle |
| Testing | 9.3 | Domain + end-to-end green |
| Documentation | 9.3 | ADR 0014 + this review |
| Coverage (service line) | **89.4%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before Phase 9 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Land service Jacoco line coverage 89.4% < 90%.** Cancel and invalid-state paths need additional tests before Phase 9 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Cadastral geometry / PostGIS parcel polygons deferred to GIS phase.
4. UNDER_TRANSFER status reserved but two-step transfer workflow not exposed in REST yet.

### Low
1. Spatial search and map views belong with Search/GIS later.
2. Lease expiry notifications deferred to Notification Platform.

## Cross-phase consistency

- Links Case/Document/Citizen/Organization by opaque IDs only
- Does not scaffold Business Licensing / Phase 9+ services

## Approval gate

**APPROVED** — Phase 9 (Business Licensing) started.
