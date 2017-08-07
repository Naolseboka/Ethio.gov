# Phase 18 Engineering Review — Analytics

**Project:** EthGov  
**Phase:** 18 — Analytics  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 19)  
**Prior phases:** Phase 0–17 APPROVED

## Independent review method

This review evaluates Phase 18 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-analytics`

| Capability | Implementation |
|---|---|
| MetricDefinition aggregate | Code, category, unit, aggregation |
| MetricObservation aggregate | Immutable period values; void path |
| Numbers | `MET-YYYY-######` / `MOB-YYYY-######` |
| AuthZ | JWT; `analytics.read` / `analytics.write` |
| Events | Outbox → `ethgov.analytics.events` |
| Schema | Flyway V1–V2; DB `ethgov_analytics`; Compose init |
| Tests | Domain + ArchUnit + register→publish→record→void |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal dual-aggregate; ArchUnit green |
| DDD | 9.4 | Clear definition vs observation separation |
| Security | 9.3 | Scoped JWT; no public mutation |
| Performance | 9.2 | Unique tenant/number/code; period indexes |
| Maintainability | 9.3 | Consistent with prior services |
| Operations | 9.2 | Port 8098, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear REST metric + observation APIs |
| Testing | 9.3 | Domain + end-to-end green |
| Documentation | 9.3 | ADR 0024 + this review |
| Coverage (service line) | **89.1%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before Phase 19 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Analytics service Jacoco line coverage 89.1% < 90%.** Retire/edge paths before Phase 19 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Auto-ingestion from Reporting/Open Data deferred.
4. OLAP/dashboard UI deferred.

### Low
1. No derived rollup materialization (query lists raw observations).
2. Multi-currency beyond ETB unit deferred.

## Cross-phase consistency

- Source references are opaque strings / typed IDs
- Does not scaffold Developer Platform / Phase 19+ services

## Approval gate

**APPROVED** — Phase 19 (Developer Platform) started.
