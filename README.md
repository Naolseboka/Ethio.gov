# Ethio.gov Platform

Digital public services for citizens and government agencies.

## Overview

This repository contains the service layer, API helpers, and operational tooling for identity, licensing, and payments workflows.

## Quick start

```bash
python -m src.main
```

## Notes

Background jobs should be idempotent and safe to retry after transient failures.

Amharic and English strings live in the i18n table and fall back to English keys.

Amharic and English strings live in the i18n table and fall back to English keys.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

Keep docker images slim; prefer multi-stage builds in CI.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

Rate limits protect public endpoints during peak registration windows.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

OCR confidence below threshold triggers a manual review queue.

Payment reconciliation runs in batches and records incomplete items for follow-up.

Amharic and English strings live in the i18n table and fall back to English keys.

Health checks must pass before a replica receives production traffic.

Services prefer explicit validation at the boundary before touching storage.

Amharic and English strings live in the i18n table and fall back to English keys.

Database migrations are forward-only; note expand/contract steps in the changelog.

OCR confidence below threshold triggers a manual review queue.

Keep docker images slim; prefer multi-stage builds in CI.

Health checks must pass before a replica receives production traffic.

Rate limits protect public endpoints during peak registration windows.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.

Background jobs should be idempotent and safe to retry after transient failures.

The gateway terminates TLS and forwards request ids to downstream services.

Payment reconciliation runs in batches and records incomplete items for follow-up.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

## License

For authorized government use.
