# Phase 1 Engineering Review — Identity Platform

**Project:** EthGov  
**Phase:** 1 — Identity Platform  
**Review date:** 2026-07-24  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 2)  
**Prior phase:** Phase 0 APPROVED

## Delivered

Deployable service: `services/ethgov-identity`

| Capability | Implementation |
|---|---|
| OAuth2/OIDC Authorization Server | Spring Authorization Server + JDBC clients/authorizations |
| User accounts | `UserAccount` aggregate (citizen, public employee, service account) |
| Password policy | Length + complexity; lockout after 5 failures / 15 minutes |
| Tenant RBAC membership | Roles + permissions per tenant |
| Admin APIs | Employee register, citizen register, membership assign, disable, OAuth client register |
| JWT claims | `ethgov_principal_type`, `ethgov_tenant_id`, `ethgov_roles`, `ethgov_permissions` |
| Events | Transactional outbox → `ethgov.identity.events` |
| Schema | Flyway V1–V3 (`ethgov_identity` DB) |
| Bootstrap | Optional first admin user + admin client |
| Tests | Domain + ArchUnit + Testcontainers API integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal packages; ArchUnit enforced |
| DDD | 9.4 | Real account/client invariants + domain events |
| Security | 9.2 | BCrypt, lockout, scoped admin APIs; ephemeral RSA keys in local/dev |
| Performance | 9.2 | Indexed username/tenant; token TTL configured |
| Maintainability | 9.4 | Clear module + use-case boundaries |
| Operations | 9.2 | Actuator probes, Compose DB init, issuer config |
| Developer Experience | 9.3 | Bootstrap flags, local port 8081 |
| Testing | 9.3 | Unit + architecture + Testcontainers integration green |
| Documentation | 9.3 | ADR 0007 + this review |
| Coverage | TBD after Jacoco aggregate on service | Domain heavily unit-tested |
| **Overall** | **9.3** | Ready for approval gate |

## Findings

### Critical
- None

### High
- None

### Medium
1. RSA signing keys are generated in-process for local/dev. Production must load keys from a secrets manager / KMS and rotate them.
2. Citizen self-registration is open (`POST /v1/citizens`). Production should add rate limiting + optional captcha/verification before public internet exposure.
3. Outbox relay to Kafka is not yet a running worker (rows are written; relay lands with notification/platform hardening or next event consumer).

### Low
1. Form-login UI is Spring default (acceptable for Authorization Code UX in Phase 1; branded login later).
2. Service-account password login correctly forbidden; client_credentials path relies on registered OAuth clients.

## Approval gate

Reply **approve Phase 1** to begin Phase 2 (Citizen Registry).
