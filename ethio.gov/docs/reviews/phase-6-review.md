# Phase 6 Engineering Review — Workflow Engine

**Project:** EthGov  
**Phase:** 6 — Workflow Engine  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 7)  
**Prior phases:** Phase 0–5 APPROVED

## Independent review method

This review evaluates Phase 6 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-workflow`

| Capability | Implementation |
|---|---|
| WorkflowDefinition | Tenant key, ordered USER_TASK steps, publish/retire |
| ProcessInstance | Start from published definition; advance/complete/cancel |
| WorkflowTask | Claim + complete (claimer-only); next-step task creation |
| AuthZ | JWT; `workflow.read` / `workflow.write` |
| Events | Outbox → `ethgov.workflow.events` |
| Schema | Flyway V1–V2; DB `ethgov_workflow`; Compose init |
| Tests | Domain + ArchUnit + two-step end-to-end integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; three aggregates; ArchUnit green |
| DDD | 9.4 | Strong lifecycle invariants; claimer-only completion |
| Security | 9.3 | Scoped JWT; no anonymous mutation |
| Performance | 9.2 | Unique tenant/key and tenant/businessKey indexes |
| Maintainability | 9.3 | Explicit engine (no opaque BPMN runtime yet) |
| Operations | 9.2 | Port 8086, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear REST for definitions, instances, tasks |
| Testing | 9.3 | Full two-step publish→start→claim→complete path |
| Documentation | 9.3 | ADR 0012 + this review |
| Coverage (service line) | **81.6%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage/BPMN-depth remediation before Phase 7 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Workflow service Jacoco line coverage 81.6% < 90%.** Cancel/retire paths need additional tests before Phase 7 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Linear USER_TASK sequences only — exclusive gateways/timers/service tasks deferred (ADR 0012).
4. Definition steps immutable after create (intentional); amendment requires new definition key/version model later.

### Low
1. Task listing by role (work queue) not yet exposed — claimant lookup only.
2. Publish save avoids step rewrite to prevent unique-constraint flush races.

## Cross-phase consistency

- Uses platform commons/web/persistence/messaging
- Does not embed Case Management / licensing domain rules
- Does not scaffold Phase 7+ services

## Approval gate

**APPROVED** — Phase 7 (Case Management) started.
