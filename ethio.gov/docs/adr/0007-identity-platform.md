# ADR 0007: Identity Platform as Authorization Server

## Status

Accepted — Phase 1

## Context

EthGov needs a single source of truth for authentication, OAuth2/OIDC clients, and tenant-scoped authority claims before Citizen Registry and downstream services can enforce access.

## Decision

Implement `services/ethgov-identity` as the first deployable service:

- Spring Authorization Server (OIDC) with JDBC-backed clients/authorizations
- Domain-owned `UserAccount` and `OAuthClient` aggregates (hexagonal)
- JWT claims aligned with Phase 0 `JwtPrincipalMapper` (`ethgov_*`)
- Transactional outbox for identity lifecycle events on `ethgov.identity.events`
- Database `ethgov_identity` (database-per-service)

Citizen self-registration is public; employee/client administration requires `SCOPE_identity.admin`.

## Consequences

- Downstream services validate JWTs against identity JWKS/issuer
- Fayda / external IdP federation is deferred to a later hardening phase
- RSA signing keys are ephemeral per process in Phase 1 local/dev; production must load keys from secret management (tracked as Medium finding)
