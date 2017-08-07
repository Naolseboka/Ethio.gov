# Phase 15 Engineering Review — Reporting

**Project:** EthGov  
**Phase:** 15 — Reporting  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 16)  
**Prior phases:** Phase 0–14 APPROVED

## Independent review method

This review evaluates Phase 15 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-reporting`

| Capability | Implementation |
|---|---|
| ReportDefinition aggregate | Code, category, format, parameter schema |
| ReportRun aggregate | Queued→running→succeeded/failed lifecycle |
| Numbers | `RPT-YYYY-######` / `RRN-YYYY-######` |
| AuthZ | JWT; `reporting.read` / `reporting.write` |
| Events | Outbox → `ethgov.reporting.events` |
| Schema | Flyway V1–V2; DB `ethgov_reporting`; Compose init |
| Tests | Domain + ArchUnit + definition→run→succeed integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal dual-aggregate; ArchUnit green |
| DDD | 9.4 | Clear catalog vs execution separation |
| Security | 9.3 | Scoped JWT; no public mutation |
| Performance | 9.2 | Unique tenant/number/code indexes |
| Maintainability | 9.3 | Consistent with prior services |
| Operations | 9.2 | Port 8095, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear REST definition + run APIs |
| Testing | 9.3 | Domain + end-to-end green |
| Documentation | 9.3 | ADR 0021 + this review |
| Coverage (service line) | **87.9%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before Phase 16 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Reporting service Jacoco line coverage 87.9% < 90%.** Retire/cancel-run edge paths need additional tests before Phase 16 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Live report query/render workers deferred.
4. Cron/scheduled report subscriptions deferred.

### Low
1. Parameter schema is structural JSON only (no JSON Schema validation engine).
2. Bulk multi-tenant report packs deferred.

## Cross-phase consistency

- Artifact references are opaque (`doc_*` or URI strings)
- Does not scaffold Search / Phase 16+ services

## Approval gate

**APPROVED** — Phase 16 (Search) started.
