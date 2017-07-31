# Capacity planning

| Tier | Services | Guidance |
|---|---|---|
| Hot path | identity, citizen, payments, gateway evaluate | HPA on CPU + p95 latency; gateway evaluate DB-backed → plan Redis counters before 5k RPS/tenant |
| Workflow | case, workflow, licensing, permits | Scale on queue depth / open cases |
| Analytical | reporting, analytics, search, opendata | Batch-friendly; separate read replicas |

Default timezone Africa/Addis_Ababa for business calendars; UTC for storage timestamps.
