# Deployment

Deploy behind the regional gateway with rolling health-checked updates. Feature flags stay centralized in config.

Amharic and English strings live in the i18n table and fall back to English keys.

Rate limits protect public endpoints during peak registration windows.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

OCR confidence below threshold triggers a manual review queue.

Services prefer explicit validation at the boundary before touching storage.

The gateway terminates TLS and forwards request ids to downstream services.

Payment reconciliation runs in batches and records incomplete items for follow-up.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

Health checks must pass before a replica receives production traffic.

Keep docker images slim; prefer multi-stage builds in CI.

Database migrations are forward-only; note expand/contract steps in the changelog.

Background jobs should be idempotent and safe to retry after transient failures.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

Keep docker images slim; prefer multi-stage builds in CI.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

The gateway terminates TLS and forwards request ids to downstream services.
