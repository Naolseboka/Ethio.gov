# Phase 13 Engineering Review — Notification Platform

**Project:** EthGov  
**Phase:** 13 — Notification Platform  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 14)  
**Prior phases:** Phase 0–12 APPROVED

## Independent review method

This review evaluates Phase 13 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-notification`

| Capability | Implementation |
|---|---|
| OutboundNotification aggregate | SMS/EMAIL/PUSH, locale, recipient, delivery state |
| Notification numbers | Tenant/year sequence → `NTF-YYYY-######` |
| Recipient validation | Ethiopian phone / email / device token by channel |
| AuthZ | JWT; `notification.read` / `notification.write` |
| Events | Outbox → `ethgov.notification.events` |
| Schema | Flyway V1–V2; DB `ethgov_notification`; Compose init |
| Tests | Domain + ArchUnit + schedule→sent→delivered integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; ArchUnit green |
| DDD | 9.4 | Channel-specific recipient invariants |
| Security | 9.3 | Scoped JWT; no public mutation |
| Performance | 9.2 | Unique tenant/number; channel/phone indexes |
| Maintainability | 9.3 | Consistent with prior services |
| Operations | 9.2 | Port 8093, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear REST notification lifecycle |
| Testing | 9.3 | Domain + end-to-end green |
| Documentation | 9.3 | ADR 0019 + this review |
| Coverage (service line) | **81.4%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before Phase 14 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Notification service Jacoco line coverage 81.4% < 90%.** Fail/cancel/email/push paths need additional tests before Phase 14 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Live SMS/email/push provider adapters deferred.
4. Rich template catalog / preference center deferred.

### Low
1. Retry/backoff worker not included (fail is explicit).
2. Bulk campaign send deferred.

## Cross-phase consistency

- Optional source references are opaque IDs only
- Does not scaffold GIS / Phase 14+ services

## Approval gate

**APPROVED** — Phase 14 (GIS) started.
