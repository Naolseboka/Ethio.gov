# ADR 0008: Citizen Registry bounded context

## Status

Accepted — Phase 2

## Context

Municipal and federal services need an authoritative registry of natural persons with Ethiopian naming, addresses, phone uniqueness, and Fayda-ready identifiers. Identity Platform (Phase 1) authenticates accounts; it does not store demographics.

## Decision

Implement `services/ethgov-citizen` as an independently deployable Citizen Registry:

- Aggregate `Citizen` owns demographics, residence address, phone, optional Fayda ID, optional link to Identity `UserAccountId`
- Database `ethgov_citizen` (database-per-service)
- JWT resource server validating tokens from Identity issuer
- Scopes: `citizen.read`, `citizen.write`
- Outbox events on `ethgov.citizen.events`

Live Fayda federation is deferred; Fayda ID is stored and uniqueness-enforced when present.

## Consequences

- Downstream licensing/case services reference `cit_*` identifiers
- Identity linking is explicit and one-to-one from the citizen side
