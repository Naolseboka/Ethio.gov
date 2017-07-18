# ADR 0009: Organization Platform bounded context

## Status

Accepted — Phase 3

## Context

Licensing, tax, procurement, and case services need an authoritative registry of legal entities (companies, NGOs, cooperatives, sole proprietors) with Ethiopian TIN, registered address, and lifecycle (active / suspended / dissolved). Citizen Registry stores natural persons; Identity authenticates accounts. Neither owns organization legal status.

## Decision

Implement `services/ethgov-organization` as an independently deployable Organization Platform:

- Aggregate `Organization` owns legal name, type, registered address, phone, optional TIN (10 digits), optional owner `cit_*` (required for sole proprietorship), and status transitions
- Database `ethgov_organization` (database-per-service)
- JWT resource server validating tokens from Identity issuer
- Scopes: `organization.read`, `organization.write`
- Outbox events on `ethgov.organization.events`
- Port `8083`

ERCA/MoR live TIN federation is deferred; TIN is validated and uniqueness-enforced when assigned.

## Consequences

- Downstream services reference `org_*` identifiers
- TIN uniqueness is enforced at application and database layers
- Government Directory (Phase 4) may reference organizations but does not replace this registry
