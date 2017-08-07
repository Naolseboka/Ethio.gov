# Phase 11 Engineering Review — Revenue Platform

**Project:** EthGov  
**Phase:** 11 — Revenue Platform  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 12)  
**Prior phases:** Phase 0–10 APPROVED

## Independent review method

This review evaluates Phase 11 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-revenue`

| Capability | Implementation |
|---|---|
| RevenueBill aggregate | Type, ETB amount, party, period/due, source links |
| Bill numbers | Tenant/year sequence → `REV-YYYY-######` |
| Integrations by ID | Optional `lic_*`, `prm_*`, `par_*`, `cas_*`; party `cit_*`/`org_*` |
| AuthZ | JWT; `revenue.read` / `revenue.write` |
| Events | Outbox → `ethgov.revenue.events` |
| Schema | Flyway V1–V2; DB `ethgov_revenue`; Compose init |
| Tests | Domain + ArchUnit + create→issue→pay integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; ArchUnit green |
| DDD | 9.4 | Clear assessment vs payment separation |
| Security | 9.3 | Scoped JWT; no public mutation |
| Performance | 9.2 | Unique tenant/billNumber; due/party indexes |
| Maintainability | 9.3 | Consistent with prior services |
| Operations | 9.2 | Port 8091, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear REST bill lifecycle |
| Testing | 9.3 | Domain + end-to-end green |
| Documentation | 9.3 | ADR 0017 + this review |
| Coverage (service line) | **84.4%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before Phase 12 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Revenue service Jacoco line coverage 84.4% < 90%.** Cancel/write-off/overdue API paths need additional tests before Phase 12 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Partial payments / installment plans deferred.
4. Actual payment capture deferred to Phase 12.

### Low
1. Automated overdue scheduler not included (overdue is an explicit command).
2. Multi-line invoice items deferred.

## Cross-phase consistency

- Links Licensing/Permits/Land/Case by opaque IDs only
- Does not scaffold Payments / Phase 12+ services

## Approval gate

**APPROVED** — Phase 12 (Payments) started.
