CREATE TABLE pay_payment (
    id                  VARCHAR(64)    PRIMARY KEY,
    tenant_id           VARCHAR(40)    NOT NULL,
    payment_number      VARCHAR(32)    NOT NULL,
    bill_id             VARCHAR(64)    NOT NULL,
    method              VARCHAR(32)    NOT NULL,
    status              VARCHAR(32)    NOT NULL,
    citizen_id          VARCHAR(64),
    organization_id     VARCHAR(64),
    amount_etb          NUMERIC(18, 2) NOT NULL,
    provider_reference  VARCHAR(120),
    idempotency_key     VARCHAR(120),
    failure_reason      VARCHAR(500),
    cancel_reason       VARCHAR(500),
    refund_reason       VARCHAR(500),
    initiated_at        TIMESTAMPTZ    NOT NULL,
    succeeded_at        TIMESTAMPTZ,
    failed_at           TIMESTAMPTZ,
    cancelled_at        TIMESTAMPTZ,
    refunded_at         TIMESTAMPTZ,
    updated_at          TIMESTAMPTZ    NOT NULL,
    version             BIGINT         NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_pay_tenant_number ON pay_payment (tenant_id, payment_number);
CREATE INDEX idx_pay_tenant_status ON pay_payment (tenant_id, status);
CREATE INDEX idx_pay_bill ON pay_payment (bill_id);
CREATE INDEX idx_pay_org ON pay_payment (organization_id);
CREATE INDEX idx_pay_citizen ON pay_payment (citizen_id);
CREATE UNIQUE INDEX uq_pay_tenant_idempotency ON pay_payment (tenant_id, idempotency_key)
    WHERE idempotency_key IS NOT NULL;

CREATE TABLE pay_number_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
