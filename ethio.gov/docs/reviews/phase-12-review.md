# Phase 12 Engineering Review — Payments

**Project:** EthGov  
**Phase:** 12 — Payments  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 13)  
**Prior phases:** Phase 0–11 APPROVED

## Independent review method

This review evaluates Phase 12 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-payments`

| Capability | Implementation |
|---|---|
| Payment aggregate | Method, ETB amount, bill link, provider ref, refund |
| Payment numbers | Tenant/year sequence → `PAY-YYYY-######` |
| Integrations by ID | Required `rev_*` bill; party `cit_*`/`org_*` |
| AuthZ | JWT; `payments.read` / `payments.write` |
| Events | Outbox → `ethgov.payments.events` |
| Schema | Flyway V1–V2; DB `ethgov_payments`; Compose init |
| Tests | Domain + ArchUnit + initiate→succeed→refund integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; ArchUnit green |
| DDD | 9.4 | Clear payment vs revenue separation |
| Security | 9.3 | Scoped JWT; no public mutation |
| Performance | 9.2 | Unique tenant/number; bill + partial unique idempotency |
| Maintainability | 9.3 | Consistent with prior services |
| Operations | 9.2 | Port 8092, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear REST payment lifecycle |
| Testing | 9.3 | Domain + end-to-end green |
| Documentation | 9.3 | ADR 0018 + this review |
| Coverage (service line) | **81.8%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before Phase 13 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Payments service Jacoco line coverage 81.8% < 90%.** Fail/cancel paths need additional tests before Phase 13 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Live PSP/gateway adapters deferred.
4. Automatic Revenue bill settlement on payment success deferred (manual/event bridge).

### Low
1. Partial refunds deferred.
2. Multi-currency beyond ETB deferred.

## Cross-phase consistency

- Links Revenue bills by opaque `rev_*` IDs only
- Does not scaffold Notification Platform / Phase 13+ services

## Approval gate

**APPROVED** — Phase 13 (Notification Platform) started.
