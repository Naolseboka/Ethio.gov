# Security

Minimize PII in application logs. Prefer token-based auth and audited admin actions.

Services prefer explicit validation at the boundary before touching storage.

Payment reconciliation runs in batches and records incomplete items for follow-up.

The gateway terminates TLS and forwards request ids to downstream services.

Health checks must pass before a replica receives production traffic.

Database migrations are forward-only; note expand/contract steps in the changelog.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

Keep docker images slim; prefer multi-stage builds in CI.

Background jobs should be idempotent and safe to retry after transient failures.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

Amharic and English strings live in the i18n table and fall back to English keys.

OCR confidence below threshold triggers a manual review queue.

Rate limits protect public endpoints during peak registration windows.

Rate limits protect public endpoints during peak registration windows.

Health checks must pass before a replica receives production traffic.

Background jobs should be idempotent and safe to retry after transient failures.

Health checks must pass before a replica receives production traffic.

OCR confidence below threshold triggers a manual review queue.

The gateway terminates TLS and forwards request ids to downstream services.

Services prefer explicit validation at the boundary before touching storage.

Payment reconciliation runs in batches and records incomplete items for follow-up.

Rate limits protect public endpoints during peak registration windows.

Keep docker images slim; prefer multi-stage builds in CI.

Background jobs should be idempotent and safe to retry after transient failures.
