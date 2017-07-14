# ADR 0003: Database per service

## Status

Accepted — Phase 0

## Context

Shared databases across government domains create coupling, blast-radius risk, and tenancy leakage.

## Decision

Each independently deployable service owns its PostgreSQL database (PostGIS where geospatial). Cross-service data access is via APIs or events—never direct SQL into another service's schema.

Flyway migrations live with the owning service.

## Consequences

- Strong isolation and independent schema evolution.
- Requires outbox/inbox and careful eventual consistency design.
