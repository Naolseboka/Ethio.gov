# ADR 0013: Case Management bounded context

## Status

Accepted — Phase 7

## Context

Licensing, complaints, land, and civil services need a governed case file that binds parties (citizen/organization), optional workflow instances, documents, notes, assignment, and status lifecycle. Workflow Engine runs steps; Document Platform stores blobs; Case Management owns the file.

## Decision

Implement `services/ethgov-case` (`et.gov.ethgov.casemgmt` package to avoid the Java `case` keyword) as an independently deployable Case Management service:

- Aggregate `GovernmentCase` — tenant-scoped `CAS-YYYY-######` numbers, type, priority, party refs, assignee, optional `ou_*` / `wpi_*` / `doc_*` links, notes
- Status machine: OPEN → IN_PROGRESS/PENDING → RESOLVED → CLOSED (plus CANCELLED)
- Database `ethgov_case`; outbox topic `ethgov.case.events`; port `8087`
- Scopes: `case.read`, `case.write`

Domain-specific licensing rules remain in later phases; this phase provides the shared case spine.

## Consequences

- Downstream services reference `cas_*` identifiers and case numbers
- Workflow/document services are linked by ID only (no cross-DB joins)
- SLA timers and multi-party participants are deferred
