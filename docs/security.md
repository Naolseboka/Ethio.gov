# Security

Minimize PII in application logs. Prefer token-based auth and audited admin actions.

Services prefer explicit validation at the boundary before touching storage.

Payment reconciliation runs in batches and records incomplete items for follow-up.

The gateway terminates TLS and forwards request ids to downstream services.

Health checks must pass before a replica receives production traffic.

Database migrations are forward-only; note expand/contract steps in the changelog.
