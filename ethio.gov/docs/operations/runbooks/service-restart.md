# Runbook: service restart

1. Confirm `/actuator/health` failing or deploy needed.
2. Drain: set K8s `replicas` or remove from Service endpoints.
3. Rolling restart via Helm upgrade / `kubectl rollout restart`.
4. Verify readiness probes green; check outbox PENDING not stuck > 5 minutes.
5. For gateway: spot-check `POST /v1/rate-limits/evaluate` with a known ACTIVE policy.
