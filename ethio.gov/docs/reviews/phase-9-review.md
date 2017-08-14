# Phase 9 Engineering Review — Business Licensing

**Project:** EthGov  
**Phase:** 9 — Business Licensing  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 10)  
**Prior phases:** Phase 0–8 APPROVED

## Independent review method

This review evaluates Phase 9 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-licensing`

| Capability | Implementation |
|---|---|
| BusinessLicense aggregate | Type, premises, holder, validity, suspension/revocation |
| License numbers | Tenant/year sequence → `LIC-YYYY-######` |
| Integrations by ID | Optional `cas_*`, `wpi_*`, `doc_*`, `par_*`; required `org_*` |
| AuthZ | JWT; `licensing.read` / `licensing.write` |
| Events | Outbox → `ethgov.licensing.events` |
| Schema | Flyway V1–V2; DB `ethgov_licensing`; Compose init |
| Tests | Domain + ArchUnit + apply→review→issue→activate→renew integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; ArchUnit green |
| DDD | 9.4 | Explicit lifecycle; organization-required holder |
| Security | 9.3 | Scoped JWT; no public mutation |
| Performance | 9.2 | Unique tenant/licenseNumber; org/case indexes |
| Maintainability | 9.3 | Consistent with prior services |
| Operations | 9.2 | Port 8089, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear REST licensing lifecycle |
| Testing | 9.3 | Domain + end-to-end green |
| Documentation | 9.3 | ADR 0015 + this review |
| Coverage (service line) | **78.0%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before Phase 10 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Licensing service Jacoco line coverage 78.0% < 90%.** Cancel/revoke/expire/reject paths need additional tests before Phase 10 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Fee assessment / payment proof deferred to Revenue/Payments phases.
4. Construction-specific permit rules deferred to Phase 10.

### Low
1. Automated expiry scheduler not included (expire is an explicit command).
2. Multi-establishment licenses under one org deferred.

## Cross-phase consistency

- Links Case/Workflow/Document/Land/Organization by opaque IDs only
- Does not scaffold Construction Permits / Phase 10+ services

## Approval gate

**APPROVED** — Phase 10 (Construction Permits) started.
