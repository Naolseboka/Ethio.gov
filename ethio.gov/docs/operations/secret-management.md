# Secret management

- Runtime secrets: Postgres passwords, OIDC client secrets, object-store keys — delivered via CSI / sealed secrets; rotated quarterly.
- Developer API keys (`egk_*`) are hashed at rest in `ethgov-developer`; plaintext shown once at issue.
- Gateway service never stores partner API keys; it evaluates quotas by opaque `dap_*` / `ack_*` IDs.
- Audit details must not contain raw secrets or PAN-like payment data.
