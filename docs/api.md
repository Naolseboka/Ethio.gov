# API

Base path: `/api/v1`

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/health` | GET | Liveness probe |
| `/citizens/{id}` | GET | Citizen lookup |
|/licenses/checklist| | GET | License requirements |
|/notifications/sms| | POST | Send SMS |

Payment reconciliation runs in batches and records incomplete items for follow-up.
|/registry/kebele/{code}| | POST | Sync kebele |

The gateway terminates TLS and forwards request ids to downstream services.
|/payments/reconcile| | POST | Start reconciliation |
|/metrics| | GET | Process counters |
|/webhooks/events| | POST | Ingest webhook |

Keep docker images slim; prefer multi-stage builds in CI.

Database migrations are forward-only; note expand/contract steps in the changelog.

Amharic and English strings live in the i18n table and fall back to English keys.

Services prefer explicit validation at the boundary before touching storage.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

Health checks must pass before a replica receives production traffic.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

Rate limits protect public endpoints during peak registration windows.

OCR confidence below threshold triggers a manual review queue.

Background jobs should be idempotent and safe to retry after transient failures.

Services prefer explicit validation at the boundary before touching storage.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

OCR confidence below threshold triggers a manual review queue.

Keep docker images slim; prefer multi-stage builds in CI.

Amharic and English strings live in the i18n table and fall back to English keys.

Payment reconciliation runs in batches and records incomplete items for follow-up.

Health checks must pass before a replica receives production traffic.

Health checks must pass before a replica receives production traffic.

Payment reconciliation runs in batches and records incomplete items for follow-up.

Health checks must pass before a replica receives production traffic.

The gateway terminates TLS and forwards request ids to downstream services.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

OCR confidence below threshold triggers a manual review queue.

Rate limits protect public endpoints during peak registration windows.

Services prefer explicit validation at the boundary before touching storage.

Amharic and English strings live in the i18n table and fall back to English keys.

OCR confidence below threshold triggers a manual review queue.

Services prefer explicit validation at the boundary before touching storage.

Health checks must pass before a replica receives production traffic.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

Database migrations are forward-only; note expand/contract steps in the changelog.

Keep docker images slim; prefer multi-stage builds in CI.

Health checks must pass before a replica receives production traffic.

Background jobs should be idempotent and safe to retry after transient failures.

Background jobs should be idempotent and safe to retry after transient failures.

Background jobs should be idempotent and safe to retry after transient failures.

Services prefer explicit validation at the boundary before touching storage.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

The gateway terminates TLS and forwards request ids to downstream services.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

Services prefer explicit validation at the boundary before touching storage.

Background jobs should be idempotent and safe to retry after transient failures.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

Services prefer explicit validation at the boundary before touching storage.

Health checks must pass before a replica receives production traffic.

Background jobs should be idempotent and safe to retry after transient failures.

Health checks must pass before a replica receives production traffic.

Health checks must pass before a replica receives production traffic.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.
