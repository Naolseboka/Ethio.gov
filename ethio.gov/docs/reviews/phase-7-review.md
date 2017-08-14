# Phase 7 Engineering Review — Case Management

**Project:** EthGov  
**Phase:** 7 — Case Management  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 8)  
**Prior phases:** Phase 0–6 APPROVED

## Independent review method

This review evaluates Phase 7 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-case`

| Capability | Implementation |
|---|---|
| GovernmentCase aggregate | Parties, priority, assignment, status machine, notes |
| Case numbers | Tenant/year sequence → `CAS-YYYY-######` |
| Integrations by ID | Optional `wpi_*` workflow, `doc_*` documents, `ou_*` owner |
| AuthZ | JWT; `case.read` / `case.write` |
| Events | Outbox → `ethgov.case.events` |
| Schema | Flyway V1–V2; DB `ethgov_case`; Compose init |
| Tests | Domain + ArchUnit + open→assign→link→close integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; ArchUnit green; package avoids Java keyword |
| DDD | 9.4 | Explicit status transitions; terminal immutability |
| Security | 9.3 | Scoped JWT; no public mutation |
| Performance | 9.2 | Unique tenant/caseNumber; party/assignee indexes |
| Maintainability | 9.3 | Consistent with prior services |
| Operations | 9.2 | Port 8087, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear REST case lifecycle |
| Testing | 9.3 | Domain + end-to-end green |
| Documentation | 9.3 | ADR 0013 + this review |
| Coverage (service line) | **81.7%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before Phase 8 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Case service Jacoco line coverage 81.7% < 90%.** Cancel and invalid-transition paths need additional tests before Phase 8 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Case-type specific business rules deferred to licensing/land phases.
4. Notes are append-only in persistence sync (by design); note deletion not supported.

### Low
1. Work-queue search by assignee/status belongs with Search Platform later.
2. Package name `casemgmt` chosen because `case` is a Java keyword.

## Cross-phase consistency

- Links Workflow/Document/Directory by opaque IDs only
- Does not scaffold Land Registry / Phase 8+ services

## Approval gate

**APPROVED** — Phase 8 (Land Registry) started.
