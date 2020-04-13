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
