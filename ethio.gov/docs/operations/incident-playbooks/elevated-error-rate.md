# Incident playbook: elevated 5xx / 429

1. Identify service via Grafana / Prometheus `http_server_requests`.
2. If 429 from gateway: inspect ACTIVE `RateLimitPolicy` and recent `gw_audit_entry` DENIED rows; raise limit or suspend abusive `ack_*` via Developer Platform.
3. If dependency timeouts: check ACTIVE `ResiliencePolicy` thresholds; suspend dependency policy while PSP/SMS recovers.
4. Append audit entry with outcome FAILURE summarizing operator action.
5. Post-incident: retire temporary policies; capture ADR/follow-up if permanent.
