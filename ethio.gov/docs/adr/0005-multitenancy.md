# ADR 0005: Multi-tenancy model

## Status

Accepted — Phase 0

## Context

Municipalities, regional governments, and federal agencies must share the platform without reading each other's data.

## Decision

- Logical tenant identifier: `TenantId` (`tnt_<uuid>`).
- Tenant type: municipality | regional government | federal agency | public institution.
- Row-level tenant ownership on tenant-scoped aggregates (`tenant_id` column, non-null, immutable).
- Request propagation via `X-EthGov-Tenant-Id` and JWT claim `ethgov_tenant_id`.
- ABAC checks deny cross-tenant access unless the principal is `SYSTEM`.

Shared reference data that is national (e.g., regional state codes) may be global and read-only.

## Consequences

- Every mutating use case must resolve and enforce tenant scope.
- Database-per-service still applies; tenancy is not achieved by separate DB per tenant in Phase 0–N (may revisit for sovereign deployments).
