# Architecture

Requests enter through an API gateway and are routed to domain services (citizens, payments, licensing). Shared libraries cover auth, caching, and metrics.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

OCR confidence below threshold triggers a manual review queue.

Background jobs should be idempotent and safe to retry after transient failures.

Keep docker images slim; prefer multi-stage builds in CI.

Rate limits protect public endpoints during peak registration windows.

Health checks must pass before a replica receives production traffic.

Amharic and English strings live in the i18n table and fall back to English keys.

Database migrations are forward-only; note expand/contract steps in the changelog.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

Payment reconciliation runs in batches and records incomplete items for follow-up.

Services prefer explicit validation at the boundary before touching storage.

The gateway terminates TLS and forwards request ids to downstream services.

Keep docker images slim; prefer multi-stage builds in CI.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

Database migrations are forward-only; note expand/contract steps in the changelog.
