-- Shared outbox/inbox schema contract for EthGov services.
-- Each service copies/adapts this into its own Flyway migrations (database-per-service).

CREATE TABLE IF NOT EXISTS platform_outbox (
    id              VARCHAR(36)  PRIMARY KEY,
    tenant_id       VARCHAR(40)  NOT NULL,
    aggregate_type  VARCHAR(128) NOT NULL,
    aggregate_id    VARCHAR(64)  NOT NULL,
    event_type      VARCHAR(256) NOT NULL,
    topic           VARCHAR(256) NOT NULL,
    partition_key   VARCHAR(128) NOT NULL,
    payload_json    TEXT         NOT NULL,
    correlation_id  VARCHAR(128) NOT NULL,
    status          VARCHAR(32)  NOT NULL,
    attempt_count   INTEGER      NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL,
    published_at    TIMESTAMPTZ,
    last_error      VARCHAR(2000),
    version         BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_outbox_status_created
    ON platform_outbox (status, created_at);

CREATE INDEX IF NOT EXISTS idx_outbox_tenant_created
    ON platform_outbox (tenant_id, created_at);

CREATE TABLE IF NOT EXISTS platform_inbox (
    event_id        VARCHAR(128) NOT NULL,
    consumer_name   VARCHAR(128) NOT NULL,
    tenant_id       VARCHAR(40)  NOT NULL,
    event_type      VARCHAR(256) NOT NULL,
    received_at     TIMESTAMPTZ  NOT NULL,
    processed_at    TIMESTAMPTZ  NOT NULL,
    PRIMARY KEY (event_id, consumer_name)
);

CREATE INDEX IF NOT EXISTS idx_inbox_tenant_received
    ON platform_inbox (tenant_id, received_at);
