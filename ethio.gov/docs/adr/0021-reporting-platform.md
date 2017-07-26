# ADR 0021: Reporting Platform bounded context

## Status

Accepted — Phase 15

## Context

Municipal and regional operators need governed operational reports (licenses issued, revenue collected, GIS feature counts) with a catalog of definitions and auditable execution jobs. Full analytics warehouses belong later (Phase 18); Phase 15 owns the report catalog and run ledger.

## Decision

Implement `services/ethgov-reporting` as an independently deployable Reporting Platform:

- Aggregate `ReportDefinition` — tenant-scoped `RPT-YYYY-######`, stable `ReportCode`, category, format (PDF/CSV/XLSX/JSON), JSON parameter schema; DRAFT → PUBLISHED → RETIRED; DRAFT → CANCELLED
- Aggregate `ReportRun` — tenant-scoped `RRN-YYYY-######`, linked to published definition; QUEUED → RUNNING → SUCCEEDED/FAILED; QUEUED → CANCELLED; artifact reference on success
- Database `ethgov_reporting`; outbox topic `ethgov.reporting.events`; port `8095`
- Scopes: `reporting.read`, `reporting.write`

No live query engines, schedulers, or BI dashboards in this phase — workers record start/succeed/fail against the run ledger.

## Consequences

- Upstream domains publish definitions by category; operators request runs by definition ID
- Artifact storage typically references Document Platform `doc_*` IDs
- Search (Phase 16) / Analytics (Phase 18) may index run metadata separately
