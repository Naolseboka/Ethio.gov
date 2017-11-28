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
