# Phase 14 Engineering Review — GIS

**Project:** EthGov  
**Phase:** 14 — GIS  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 15)  
**Prior phases:** Phase 0–13 APPROVED

## Independent review method

This review evaluates Phase 14 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration (PostGIS contains), and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-gis`

| Capability | Implementation |
|---|---|
| GisFeature aggregate | Types, WKT geometry, parcel/permit refs |
| Feature numbers | Tenant/year sequence → `GIS-YYYY-######` |
| Spatial query | PostGIS `ST_Contains` point-in-feature |
| AuthZ | JWT; `gis.read` / `gis.write` |
| Events | Outbox → `ethgov.gis.events` |
| Schema | Flyway V1–V2 + PostGIS extension; DB `ethgov_gis`; Compose init |
| Tests | Domain + ArchUnit + register→publish→contains→retire |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; ArchUnit green |
| DDD | 9.4 | Clear draft/publish lifecycle; parcel invariant |
| Security | 9.3 | Scoped JWT; no public mutation |
| Performance | 9.2 | Unique tenant/number; status/type indexes |
| Maintainability | 9.3 | Consistent with prior services |
| Operations | 9.2 | Port 8094, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear REST + contains endpoint |
| Testing | 9.3 | Domain + PostGIS integration green |
| Documentation | 9.3 | ADR 0020 + this review |
| Coverage (service line) | **88.1%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before Phase 15 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **GIS service Jacoco line coverage 88.1% < 90%.** Cancel/lookup edge paths need additional tests before Phase 15 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Native geometry column + GiST index deferred (WKT + `ST_GeomFromText` at query time).
4. Vector tiles / editing UI deferred.

### Low
1. Soft Ethiopia bounding-box guardrail not enforced in API.
2. Multi-geometry topology repair tools deferred.

## Cross-phase consistency

- Optional parcel/permit IDs are opaque prefixed references only
- Does not scaffold Reporting / Phase 15+ services

## Approval gate

**APPROVED** — Phase 15 (Reporting) started.
