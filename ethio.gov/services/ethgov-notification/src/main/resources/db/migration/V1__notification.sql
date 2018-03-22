CREATE TABLE ntf_notification (
    id                   VARCHAR(64)   PRIMARY KEY,
    tenant_id            VARCHAR(40)   NOT NULL,
    notification_number  VARCHAR(32)   NOT NULL,
    channel              VARCHAR(16)   NOT NULL,
    status               VARCHAR(32)   NOT NULL,
    phone_e164           VARCHAR(20),
    email                VARCHAR(254),
    device_token         VARCHAR(512),
    locale               VARCHAR(8)    NOT NULL,
    subject              VARCHAR(200),
    body                 VARCHAR(4000) NOT NULL,
    template_code        VARCHAR(80),
    citizen_id           VARCHAR(64),
    organization_id      VARCHAR(64),
    source_reference     VARCHAR(64),
    provider_reference   VARCHAR(120),
    failure_reason       VARCHAR(500),
    cancel_reason        VARCHAR(500),
    scheduled_at         TIMESTAMPTZ   NOT NULL,
    sent_at              TIMESTAMPTZ,
    delivered_at         TIMESTAMPTZ,
    failed_at            TIMESTAMPTZ,
    cancelled_at         TIMESTAMPTZ,
    updated_at           TIMESTAMPTZ   NOT NULL,
    version              BIGINT        NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_ntf_tenant_number ON ntf_notification (tenant_id, notification_number);
CREATE INDEX idx_ntf_tenant_status ON ntf_notification (tenant_id, status);
CREATE INDEX idx_ntf_channel ON ntf_notification (channel);
CREATE INDEX idx_ntf_phone ON ntf_notification (phone_e164);
CREATE INDEX idx_ntf_email ON ntf_notification (email);
CREATE INDEX idx_ntf_source ON ntf_notification (source_reference);

CREATE TABLE ntf_number_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
