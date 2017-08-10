# Security

Minimize PII in application logs. Prefer token-based auth and audited admin actions.

Services prefer explicit validation at the boundary before touching storage.

Payment reconciliation runs in batches and records incomplete items for follow-up.
