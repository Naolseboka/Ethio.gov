# Architecture

Requests enter through an API gateway and are routed to domain services (citizens, payments, licensing). Shared libraries cover auth, caching, and metrics.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

OCR confidence below threshold triggers a manual review queue.

Background jobs should be idempotent and safe to retry after transient failures.
