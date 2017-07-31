# Backup and disaster recovery

## Postgres

- Nightly logical dumps per `ethgov_*` database (retain 30 days).
- Continuous WAL archiving for RPO ≤ 15 minutes on transactional services (identity, payments, land, case).
- Gateway counters (`gw_rate_counter`) are ephemeral; policies and audit must be restored.

## Kafka

- Topic retention ≥ 7 days for `ethgov.*.events`.
- Outbox tables are the source of truth until publish ACK; restore DB before replaying.

## DR drill

1. Restore Postgres to staging from latest backup.
2. Replay outbox PENDING rows after broker recovery.
3. Verify gateway evaluate returns 404 for missing policies until policy restore completes.
