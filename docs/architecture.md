# Architecture

Requests enter through an API gateway and are routed to domain services (citizens, payments, licensing). Shared libraries cover auth, caching, and metrics.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

OCR confidence below threshold triggers a manual review queue.

Background jobs should be idempotent and safe to retry after transient failures.

Keep docker images slim; prefer multi-stage builds in CI.

Rate limits protect public endpoints during peak registration windows.

Health checks must pass before a replica receives production traffic.

Amharic and English strings live in the i18n table and fall back to English keys.
