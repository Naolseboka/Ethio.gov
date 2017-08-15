# Architecture

Requests enter through an API gateway and are routed to domain services (citizens, payments, licensing). Shared utilities handle auth, caching, and metrics.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.
