# Phase 17 Engineering Review — Open Data

**Project:** EthGov  
**Phase:** 17 — Open Data  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 18)  
**Prior phases:** Phase 0–16 APPROVED

## Independent review method

This review evaluates Phase 17 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-opendata`

| Capability | Implementation |
|---|---|
| OpenDataset aggregate | Slug, theme, license, publisher, locale |
| DatasetResource aggregate | Format, access URL / doc_* |
| Numbers | `ODS-YYYY-######` / `ODR-YYYY-######` |
| AuthZ | JWT; `opendata.read` / `opendata.write` |
| Events | Outbox → `ethgov.opendata.events` |
| Schema | Flyway V1–V2; DB `ethgov_opendata`; Compose init |
| Tests | Domain + ArchUnit + register→publish→resource integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal dual-aggregate; ArchUnit green |
| DDD | 9.4 | Clear catalog vs distribution separation |
| Security | 9.3 | Scoped JWT; no public mutation |
| Performance | 9.2 | Unique tenant/number/slug indexes |
| Maintainability | 9.3 | Consistent with prior services |
| Operations | 9.2 | Port 8097, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear REST dataset + resource APIs |
| Testing | 9.3 | Domain + end-to-end green |
| Documentation | 9.3 | ADR 0023 + this review |
| Coverage (service line) | **86.0%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before Phase 18 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Open Data service Jacoco line coverage 86.0% < 90%.** Update-resource/lookup edge paths before Phase 18 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Anonymous public catalog read deferred to gateway.
4. CKAN/DCAT-AP export feed deferred.

### Low
1. Dataset must have ≥1 resource before publish not enforced (metadata-only publish allowed).
2. Harvesting from Reporting/GIS exports deferred.

## Cross-phase consistency

- Publisher/document references are opaque typed IDs
- Does not scaffold Analytics / Phase 18+ services

## Approval gate

**APPROVED** — Phase 18 (Analytics) started.
