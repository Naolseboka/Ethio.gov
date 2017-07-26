# ADR 0020: GIS Platform bounded context

## Status

Accepted — Phase 14

## Context

Land Registry (Phase 8) deferred full cadastral geometry. Municipal planning, permits, and parcel services need authoritative spatial features (boundaries, footprints, zones) with point-in-feature queries over WGS84.

## Decision

Implement `services/ethgov-gis` as an independently deployable GIS Platform:

- Aggregate `GisFeature` — tenant-scoped `GIS-YYYY-######` numbers; types PARCEL_BOUNDARY / BUILDING_FOOTPRINT / ZONE / ROAD / POINT_OF_INTEREST / OTHER
- Geometry stored as WKT (SRID 4326); spatial contains via PostGIS `ST_Contains`
- Status machine: DRAFT → PUBLISHED → RETIRED; DRAFT → CANCELLED
- Optional `par_*` / `prm_*` references; PARCEL_BOUNDARY requires `parcelId`
- Database `ethgov_gis`; outbox topic `ethgov.gis.events`; port `8094`
- Scopes: `gis.read`, `gis.write`

No vector tile server, editing UI, or cross-service geometry sync in this phase.

## Consequences

- Land / permits link features by opaque IDs
- Topology validation beyond WKT shape checks relies on PostGIS at query time
- Reporting (Phase 15) may aggregate published feature counts by type/status
