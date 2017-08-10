# Phase 2 Engineering Review — Citizen Registry

**Project:** EthGov  
**Phase:** 2 — Citizen Registry  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 3)  
**Prior phases:** Phase 0 APPROVED · Phase 1 APPROVED

## Independent review method

This review evaluates Phase 2 in isolation against the project quality bar, without assuming Phase 1 approval implies Phase 2 readiness. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration, and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-citizen`

| Capability | Implementation |
|---|---|
| Citizen aggregate | Ethiopian naming (given/father/grandfather), DOB, sex, residence address, phone |
| Uniqueness | Phone E.164 unique; Fayda ID unique when present |
| Fayda readiness | Validated 12–16 digit ID; no fake Fayda API client |
| Identity link | Optional one-to-one link to Identity `usr_*` account |
| Lifecycle | Register, update demographics, assign Fayda, link identity, record death |
| AuthZ | JWT resource server; `citizen.read` / `citizen.write` scopes |
| Events | Outbox → `ethgov.citizen.events` |
| Schema | Flyway V1–V2; DB `ethgov_citizen` |
| Tests | Domain + ArchUnit + Testcontainers integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; ArchUnit green |
| DDD | 9.4 | Strong invariants (DOB, death, immutability of deceased, Fayda/identity once-assigned) |
| Security | 9.3 | Stateless JWT; scoped write/read; no public mutation |
| Performance | 9.2 | Unique indexes on phone/Fayda; name search key indexed |
| Maintainability | 9.4 | Mirrors Identity service patterns without copy-paste dumps |
| Operations | 9.2 | Port 8082, health probes, Compose DB init script |
| Developer Experience | 9.3 | Clear DTOs; test JWT profile |
| Testing | 9.3 | Unit + architecture + integration green |
| Documentation | 9.3 | ADR 0008 + this review |
| Coverage (service line) | **68.3%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.2** | Approve with coverage remediation before Phase 3 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Citizen service Jacoco line coverage 68.3% < 90%.** Persistence mappers/controllers need additional tests before Phase 3 merge freeze.
2. Citizen search is phone/Fayda exact match only. Fuzzy name search belongs with Search Platform (Phase 16), not ad-hoc SQL here.
3. Outbox relay to Kafka still not a running worker (consistent with Phase 1 finding).
4. Resource server trusts Identity issuer; local/dev requires Identity reachable or test profile JwtDecoder.

### Low
1. Partial address update API not offered — demographics update replaces the residence block intentionally.
2. `Sex.UNKNOWN` allowed for edge registrations; policy may tighten per federal guidance later.

## Cross-phase consistency

- Uses platform commons (phone, address, calendar, tenant, RFC7807, outbox)
- Does not embed Identity authentication logic
- Does not scaffold Organization/Directory services

## Approval gate

**APPROVED** — Phase 3 (Organization Platform) started.
