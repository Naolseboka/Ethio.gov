# Phase 3 Engineering Review — Organization Platform

**Project:** EthGov  
**Phase:** 3 — Organization Platform  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 4)  
**Prior phases:** Phase 0 APPROVED · Phase 1 APPROVED · Phase 2 APPROVED

## Independent review method

This review evaluates Phase 3 in isolation against the project quality bar, without assuming Phase 2 approval implies Phase 3 readiness. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-organization`

| Capability | Implementation |
|---|---|
| Organization aggregate | Legal name, trade name, type, registered address, phone, email, optional owner `cit_*` |
| Types | PRIVATE_COMPANY, SOLE_PROPRIETORSHIP, PARTNERSHIP, NGO, COOPERATIVE, PUBLIC_ENTERPRISE, GOVERNMENT_BODY, OTHER |
| TIN | 10-digit value object; unique when assigned; idempotent same-TIN assign |
| Lifecycle | Register, update profile, assign TIN, suspend, reinstate, dissolve |
| AuthZ | JWT resource server; `organization.read` / `organization.write` scopes |
| Events | Outbox → `ethgov.organization.events` |
| Schema | Flyway V1–V2; DB `ethgov_organization`; Compose init `03-organization.sql` |
| Tests | Domain + ArchUnit + Testcontainers integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; ArchUnit green; mirrors Citizen patterns |
| DDD | 9.4 | Strong invariants (sole-prop owner, TIN once, suspended/dissolved immutability) |
| Security | 9.3 | Stateless JWT; scoped write/read; no public mutation |
| Performance | 9.2 | Partial unique index on TIN; tenant/name/status indexes |
| Maintainability | 9.4 | Consistent with Identity/Citizen without copy-paste dumps |
| Operations | 9.2 | Port 8083, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear DTOs; test JWT profile |
| Testing | 9.3 | Unit + architecture + integration green |
| Documentation | 9.3 | ADR 0009 + this review |
| Coverage (service line) | **78.3%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before Phase 4 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Organization service Jacoco line coverage 78.3% < 90%.** Update/reinstate/dissolve paths and error branches need additional tests before Phase 4 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with Phases 1–2).
3. Resource server trusts Identity issuer; local/dev requires Identity reachable or test profile JwtDecoder.
4. ERCA/MoR live TIN federation deferred by design (ADR 0009); uniqueness is local only.

### Low
1. Organization search is TIN exact match only. Fuzzy legal-name search belongs with Search Platform (Phase 16).
2. Phone uniqueness across organizations is not enforced (TIN is the legal unique key); policy may tighten later.

## Cross-phase consistency

- Uses platform commons (phone, address, tenant, RFC7807, outbox)
- Does not embed Identity authentication logic or Citizen demographics
- Does not scaffold Government Directory / Phase 4+ services
- Removed accidental empty `services/ethgov-citizen-registry` scaffold discovered during Phase 3

## Approval gate

**APPROVED** — Phase 4 (Government Directory) started.
