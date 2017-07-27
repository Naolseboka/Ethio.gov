# ADR 0024: Analytics Platform bounded context

## Status

Accepted — Phase 18

## Context

Operators need governed KPI definitions and immutable period observations (licenses issued, ETB collected, case backlog) without embedding warehouse logic into every transactional service. Full BI cubes and streaming pipelines belong later.

## Decision

Implement `services/ethgov-analytics` as an independently deployable Analytics Platform:

- Aggregate `MetricDefinition` — tenant-scoped `MET-YYYY-######`, stable code, category, unit, aggregation; DRAFT → PUBLISHED → RETIRED; DRAFT → CANCELLED
- Aggregate `MetricObservation` — tenant-scoped `MOB-YYYY-######`, period bounds, numeric value, optional dimensions JSON and source reference; RECORDED → VOIDED (immutable values)
- Observations only against PUBLISHED metrics
- Database `ethgov_analytics`; outbox topic `ethgov.analytics.events`; port `8098`
- Scopes: `analytics.read`, `analytics.write`

OLAP cubes, dashboards, and auto-ingestion from Reporting/Open Data are deferred.

## Consequences

- Reporting runs and Open Data releases can cite observation source references
- Developer Platform (Phase 19) may expose read APIs for partner dashboards
- Corrections are void-and-re-record, preserving auditability
