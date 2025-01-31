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

Services prefer explicit validation at the boundary before touching storage.

The gateway terminates TLS and forwards request ids to downstream services.

Services prefer explicit validation at the boundary before touching storage.

Keep docker images slim; prefer multi-stage builds in CI.

Database migrations are forward-only; note expand/contract steps in the changelog.

The gateway terminates TLS and forwards request ids to downstream services.

Keep docker images slim; prefer multi-stage builds in CI.

Database migrations are forward-only; note expand/contract steps in the changelog.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

Payment reconciliation runs in batches and records incomplete items for follow-up.

OCR confidence below threshold triggers a manual review queue.

Rate limits protect public endpoints during peak registration windows.

Services prefer explicit validation at the boundary before touching storage.

Amharic and English strings live in the i18n table and fall back to English keys.

Background jobs should be idempotent and safe to retry after transient failures.

Health checks must pass before a replica receives production traffic.

Database migrations are forward-only; note expand/contract steps in the changelog.

Keep docker images slim; prefer multi-stage builds in CI.

OCR confidence below threshold triggers a manual review queue.

Payment reconciliation runs in batches and records incomplete items for follow-up.

Database migrations are forward-only; note expand/contract steps in the changelog.

Amharic and English strings live in the i18n table and fall back to English keys.

Background jobs should be idempotent and safe to retry after transient failures.

Background jobs should be idempotent and safe to retry after transient failures.

Keep docker images slim; prefer multi-stage builds in CI.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

The gateway terminates TLS and forwards request ids to downstream services.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

Background jobs should be idempotent and safe to retry after transient failures.

Services prefer explicit validation at the boundary before touching storage.
