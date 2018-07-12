CREATE TABLE rev_bill (
    id                  VARCHAR(64)    PRIMARY KEY,
    tenant_id           VARCHAR(40)    NOT NULL,
    bill_number         VARCHAR(32)    NOT NULL,
    revenue_type        VARCHAR(40)    NOT NULL,
    description         VARCHAR(2000)  NOT NULL,
    status              VARCHAR(32)    NOT NULL,
    citizen_id          VARCHAR(64),
    organization_id     VARCHAR(64),
    amount_etb          NUMERIC(18, 2) NOT NULL,
    period_start        TIMESTAMPTZ,
    period_end          TIMESTAMPTZ,
    due_at              TIMESTAMPTZ,
    issued_at           TIMESTAMPTZ,
    paid_at             TIMESTAMPTZ,
    payment_reference   VARCHAR(120),
    license_id          VARCHAR(64),
    permit_id           VARCHAR(64),
    parcel_id           VARCHAR(64),
    linked_case_id      VARCHAR(64),
    cancel_reason       VARCHAR(500),
    write_off_reason    VARCHAR(500),
    created_at          TIMESTAMPTZ    NOT NULL,
    updated_at          TIMESTAMPTZ    NOT NULL,
    version             BIGINT         NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_rev_tenant_number ON rev_bill (tenant_id, bill_number);
CREATE INDEX idx_rev_tenant_status ON rev_bill (tenant_id, status);
CREATE INDEX idx_rev_org ON rev_bill (organization_id);
CREATE INDEX idx_rev_citizen ON rev_bill (citizen_id);
CREATE INDEX idx_rev_due ON rev_bill (due_at);
CREATE INDEX idx_rev_license ON rev_bill (license_id);
CREATE INDEX idx_rev_permit ON rev_bill (permit_id);
CREATE INDEX idx_rev_parcel ON rev_bill (parcel_id);

CREATE TABLE rev_number_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
