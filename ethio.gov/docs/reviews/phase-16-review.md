# Phase 16 Engineering Review — Search

**Project:** EthGov  
**Phase:** 16 — Search  
**Review date:** 2026-07-25  
**Status:** APPROVED — 2026-07-25 (proceed to Phase 17)  
**Prior phases:** Phase 0–15 APPROVED

## Independent review method

This review evaluates Phase 16 in isolation against the project quality bar. Evidence: source inspection, ArchUnit rules, domain unit tests, Testcontainers API integration (FTS + trigram), and `./gradlew check`.

## Delivered

Deployable service: `services/ethgov-search`

| Capability | Implementation |
|---|---|
| SearchDocument aggregate | Source type/id, title/body, locale, tags |
| Document numbers | Tenant/year sequence → `SDC-YYYY-######` |
| Query | PostgreSQL FTS + `pg_trgm` similarity |
| Upsert | Re-index by tenant+source; reactivate retired |
| AuthZ | JWT; `search.read` / `search.write` |
| Events | Outbox → `ethgov.search.events` |
| Schema | Flyway V1–V2 + pg_trgm; DB `ethgov_search`; Compose init |
| Tests | Domain + ArchUnit + index→query→retire integration |

## Scores

| Dimension | Score | Notes |
|---|---:|---|
| Architecture | 9.4 | Hexagonal; ArchUnit green |
| DDD | 9.4 | Clear index document + query use case |
| Security | 9.3 | Scoped JWT; no public mutation |
| Performance | 9.2 | Unique source key; GIN FTS + trigram indexes |
| Maintainability | 9.3 | Consistent with prior services |
| Operations | 9.2 | Port 8096, health probes, Compose DB init |
| Developer Experience | 9.3 | Clear index + `/v1/search` query API |
| Testing | 9.3 | Domain + FTS/trigram integration green |
| Documentation | 9.3 | ADR 0022 + this review |
| Coverage (service line) | **87.4%** | Below ≥90% program target — Medium finding |
| **Overall** | **9.3** | Approve with coverage remediation before Phase 17 freeze |

## Findings

### Critical
- None

### High
- None

### Medium
1. **Search service Jacoco line coverage 87.4% < 90%.** Lookup/reactivate edge paths need additional tests before Phase 17 merge freeze.
2. Outbox relay to Kafka still not a running worker (consistent with prior phases).
3. Auto-indexing consumers from upstream events deferred.
4. OpenSearch/Elasticsearch adapter deferred.

### Low
1. Source-type filter applied in-memory after FTS fetch (over-fetch when filtering).
2. Locale-specific stemming beyond `simple` config deferred.

## Cross-phase consistency

- Source IDs are opaque typed prefixes (`cit_*`, `org_*`, …)
- Does not scaffold Open Data / Phase 17+ services

## Approval gate

**APPROVED** — Phase 17 (Open Data) started.
