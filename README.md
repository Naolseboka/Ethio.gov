# Ethio.gov Platform

Digital public services for citizens and government agencies.

## Overview

This repository contains the service layer, API helpers, and operational tooling for identity, licensing, and payments workflows.

## Quick start

```bash
python -m src.main
```

## Notes

The gateway terminates TLS and forwards request ids to downstream services.

Payment reconciliation runs in batches and records incomplete items for follow-up.

Regional rollouts are gated by feature flags in `config/feature_flags.yaml`.

## License

For authorized government use.
