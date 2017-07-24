# ADR 0016: Construction Permits bounded context

## Status

Accepted — Phase 10

## Context

Municipal building control needs a governed lifecycle for construction applications tied to land parcels: review, issuance, active works, suspension, completion, and revocation. Business Licensing covers trade activity; Construction Permits cover structural works on parcels.

## Decision

Implement `services/ethgov-permits` as an independently deployable Construction Permits service:

- Aggregate `ConstructionPermit` — tenant-scoped `PRM-YYYY-######` numbers, permit type, structure use, floors, floor area, optional ETB estimated cost, required `par_*` parcel, applicant `cit_*`/`org_*`, optional `cas_*` / `wpi_*` / `doc_*` links
- Status machine: APPLIED → UNDER_REVIEW → ISSUED → ACTIVE → COMPLETED; ACTIVE ↔ SUSPENDED; ACTIVE/SUSPENDED → REVOKED; APPLIED/UNDER_REVIEW → REJECTED/CANCELLED; ISSUED/ACTIVE/SUSPENDED → EXPIRED
- Database `ethgov_permits`; outbox topic `ethgov.permits.events`; port `8090`
- Scopes: `permits.read`, `permits.write`

Inspections scheduling and fee/payment proof remain later phases (Revenue/Payments).

## Consequences

- Downstream services reference `prm_*` identifiers and permit numbers
- Land Registry parcels are linked by ID only (no cross-DB joins)
- Building code rule engines deferred
