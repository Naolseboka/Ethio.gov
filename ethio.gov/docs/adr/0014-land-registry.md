# ADR 0014: Land Registry bounded context

## Status

Accepted — Phase 8

## Context

Municipal and regional land administration needs an authoritative parcel registry for ownership, tenure, land use, and title/lease metadata. Full cadastral geometry and GIS analysis belong later (Phase 14); Phase 8 must still produce a production-quality registry spine that Cases and Documents can reference.

## Decision

Implement `services/ethgov-land` as an independently deployable Land Registry service:

- Aggregate `LandParcel` — tenant-scoped `LND-YYYY-######` numbers, Ethiopian address fields (region/zone/woreda/kebele), land use, tenure (LEASEHOLD primary), area, citizen/org ownership, optional title/lease refs, optional `cas_*` / `doc_*` links
- Status machine: ACTIVE ↔ ENCUMBERED; ACTIVE/ENCUMBERED → RETIRED; non-retired → CANCELLED
- Database `ethgov_land`; outbox topic `ethgov.land.events`; port `8088`
- Scopes: `land.read`, `land.write`
- Geometry/survey layers deferred; registry stores administrative metadata only

## Consequences

- Downstream services reference `par_*` identifiers and parcel numbers
- Ownership transfers and encumbrances emit outbox events for Search/Notification later
- GIS and survey workflows remain Phase 14 concerns
