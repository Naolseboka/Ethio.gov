# ADR 0015: Business Licensing bounded context

## Status

Accepted — Phase 9

## Context

Municipal trade and professional licensing needs a governed lifecycle for applications, review, issuance, suspension, revocation, and renewal. Case Management owns the file; Workflow Engine runs steps; Organization/Citizen hold parties; Document Platform stores evidence. Licensing owns the license itself.

## Decision

Implement `services/ethgov-licensing` as an independently deployable Business Licensing service:

- Aggregate `BusinessLicense` — tenant-scoped `LIC-YYYY-######` numbers, license type, business name, premises address, required `org_*` holder (optional `cit_*`), optional `par_*` / `cas_*` / `wpi_*` / `doc_*` links
- Status machine: APPLIED → UNDER_REVIEW → ISSUED → ACTIVE; ACTIVE ↔ SUSPENDED; ACTIVE/SUSPENDED → REVOKED/EXPIRED; APPLIED/UNDER_REVIEW → REJECTED/CANCELLED
- Database `ethgov_licensing`; outbox topic `ethgov.licensing.events`; port `8089`
- Scopes: `licensing.read`, `licensing.write`

Specialized permit regimes (construction) remain Phase 10; payments/fees remain Phase 11–12.

## Consequences

- Downstream services reference `lic_*` identifiers and license numbers
- Case/workflow/document/land are linked by ID only
- Fee calculation and receipting are deferred to Revenue/Payments
