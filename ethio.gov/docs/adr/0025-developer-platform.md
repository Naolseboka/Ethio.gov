# ADR 0025: Developer Platform bounded context

## Status

Accepted — Phase 19

## Context

Partners and municipal integrators need registered applications, scoped allow-lists, and rotatable API credentials without embedding key management into Identity or every transactional service. Full API Gateway rate limiting and public developer portals belong with Production Hardening.

## Decision

Implement `services/ethgov-developer` as an independently deployable Developer Platform:

- Aggregate `DeveloperApp` — tenant-scoped `DAP-YYYY-######`, public slug, contact email, allowed scopes JSON; DRAFT → ACTIVE ↔ SUSPENDED → RETIRED; DRAFT → CANCELLED
- Aggregate `ApiCredential` — tenant-scoped `ACK-YYYY-######`, SHA-256 hashed secret, visible prefix, optional expiry; ACTIVE → REVOKED; plaintext returned once on issue
- Credentials only for ACTIVE apps
- Database `ethgov_developer`; outbox topic `ethgov.developer.events`; port `8099`
- Scopes: `developer.read`, `developer.write`

OAuth client-credentials federation with Identity, webhook subscriptions, and public self-service signup are deferred.

## Consequences

- Gateway/Identity can look up credentials by hash/prefix
- Production Hardening (Phase 20) can add quota/rate-limit policies keyed by `dap_*` / `ack_*`
- Secrets never persist in plaintext
