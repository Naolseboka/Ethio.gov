# ADR 0023: Open Data Platform bounded context

## Status

Accepted — Phase 17

## Context

Municipal and federal institutions need a governed public data catalog (DCAT-style) for land extracts, license statistics, and GIS downloads. Embedding publication into every transactional service couples release policy to operational APIs.

## Decision

Implement `services/ethgov-opendata` as an independently deployable Open Data Platform:

- Aggregate `OpenDataset` — tenant-scoped `ODS-YYYY-######`, public `slug`, theme, open license, optional publisher `org_*`, DRAFT → PUBLISHED → RETIRED; DRAFT → CANCELLED
- Aggregate `DatasetResource` — tenant-scoped `ODR-YYYY-######`, format (CSV/JSON/GeoJSON/XLSX/PDF/API), `https` access URL and/or Document Platform `doc_*`
- Resources allowed on DRAFT or PUBLISHED datasets only
- Database `ethgov_opendata`; outbox topic `ethgov.opendata.events`; port `8097`
- Scopes: `opendata.read`, `opendata.write`

Anonymous public portal access, CKAN import, and automated dataset harvesting are deferred (API Gateway / Developer Platform).

## Consequences

- Agencies publish catalog metadata independently of source systems
- Analytics (Phase 18) may aggregate published dataset/resource counts
- Binary hosting remains Document Platform or external HTTPS URLs
