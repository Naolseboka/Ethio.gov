# ADR 0022: Search Platform bounded context

## Status

Accepted — Phase 16

## Context

Citizen, organization, and directory domains deferred fuzzy name search. Operators need a cross-domain searchable index with full-text and approximate matching, without embedding OpenSearch into every transactional service.

## Decision

Implement `services/ethgov-search` as an independently deployable Search Platform:

- Aggregate `SearchDocument` — tenant-scoped `SDC-YYYY-######`, unique `(tenant, sourceType, sourceId)`, title/summary/body, locale, tags
- Status: ACTIVE ↔ RETIRED (reactivate via re-index upsert)
- Query via PostgreSQL `tsvector` full-text + `pg_trgm` similarity (no OpenSearch dependency in this phase)
- Upsert indexing API for source systems; query API with optional source-type filter
- Database `ethgov_search`; outbox topic `ethgov.search.events`; port `8096`
- Scopes: `search.read`, `search.write`

Kafka consumers that auto-index upstream events, multilingual analyzers beyond `simple`, and OpenSearch/Elasticsearch adapters are deferred.

## Consequences

- Upstream services (or workers) call index/upsert with opaque source IDs
- Exact-match lookups remain in source services; fuzzy/full-text belongs here
- Open Data / Analytics may consume the same index metadata later
