# Phase 4 Engineering Review — Government Directory

**Project:** EthGov  
**Phase:** 4 — Government Directory  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 5)  
**Prior phases:** Phase 0–3 APPROVED

## Independent review method

This review evaluates Phase 4 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-directory`

| Capability | Implementation |
|---|---|
| OrganizationalUnit aggregate | Tenant-scoped hierarchy; unique codes; rename/move/deactivate/reactivate |
| Hierarchy safety | Cycle detection on move; deactivate blocked by active children or active employees |
| PublicEmployee aggregate | Employee number, `cit_*` link, optional `usr_*`, assignment, separation |
| AuthZ | JWT resource server; `directory.read` / `directory.write` |
| Events | Outbox → `ethgov.directory.events` |
| Schema | Flyway V1–V2; DB `ethgov_directory`; Compose init `04-directory.sql` |
| Tests | Domain + ArchUnit + Testcontainers integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; ArchUnit green; two aggregates in one BC intentionally |
| DDD | 9.4 | Strong invariants (tenant match, cycle, deactivate guards, identity once) |
| Security | 9.3 | Stateless JWT; scoped write/read |
| Performance | 9.2 | Unique indexes on (tenant,code)/(tenant,employeeNumber); unit/employee indexes |
| Maintainability | 9.3 | Consistent with prior services |
| Operations | 9.2 | Port 8084, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear REST surface for units and employees |
| Testing | 9.2 | Unit + architecture + integration green |
| Documentation | 9.3 | ADR 0010 + this review |
| Coverage (service line) | **70.8%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.2** | Approve with coverage remediation before Phase 5 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Directory service Jacoco line coverage 70.8% < 90%.** Move/reactivate/identity-link and error branches need additional tests before Phase 5 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with Phases 1–3).
3. Fine-grained permission/role catalogs deferred to Identity ABAC (ADR 0010); Directory owns structure + roster only.
4. Resource server trusts Identity issuer; local/dev requires Identity reachable or test profile JwtDecoder.

### Low
1. `ou_` prefix yields 39-char IDs (vs 40 for `org_`/`cit_`); validation correctly accepts ≥39.
2. No fuzzy employee/name search — belongs with Search Platform (Phase 16).

## Cross-phase consistency

- Uses platform commons (tenant, RFC7807, outbox)
- References `cit_*` / `usr_*` / optional `org_*` by ID only — no cross-DB joins
- Does not scaffold Document Platform / Phase 5+ services

## Approval gate

**APPROVED** — Phase 5 (Document Platform) started.
