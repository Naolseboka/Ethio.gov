CREATE TABLE lnd_parcel (
    id                  VARCHAR(64)    PRIMARY KEY,
    tenant_id           VARCHAR(40)    NOT NULL,
    parcel_number       VARCHAR(32)    NOT NULL,
    region              VARCHAR(120)   NOT NULL,
    zone                VARCHAR(120),
    woreda              VARCHAR(120)   NOT NULL,
    kebele              VARCHAR(120),
    locality            VARCHAR(200),
    land_use            VARCHAR(32)    NOT NULL,
    tenure_type         VARCHAR(32)    NOT NULL,
    status              VARCHAR(32)    NOT NULL,
    area_sq_m           NUMERIC(14, 2) NOT NULL,
    citizen_id          VARCHAR(64),
    organization_id     VARCHAR(64),
    title_deed_number   VARCHAR(120),
    lease_reference     VARCHAR(120),
    lease_expires_at    TIMESTAMPTZ,
    linked_case_id      VARCHAR(64),
    encumbrance_reason  VARCHAR(500),
    registered_at       TIMESTAMPTZ    NOT NULL,
    updated_at          TIMESTAMPTZ    NOT NULL,
    version             BIGINT         NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_lnd_tenant_number ON lnd_parcel (tenant_id, parcel_number);
CREATE INDEX idx_lnd_tenant_status ON lnd_parcel (tenant_id, status);
CREATE INDEX idx_lnd_citizen ON lnd_parcel (citizen_id);
CREATE INDEX idx_lnd_org ON lnd_parcel (organization_id);
CREATE INDEX idx_lnd_case ON lnd_parcel (linked_case_id);

CREATE TABLE lnd_parcel_document (
    parcel_id   VARCHAR(64) NOT NULL REFERENCES lnd_parcel (id) ON DELETE CASCADE,
    document_id VARCHAR(64) NOT NULL,
    PRIMARY KEY (parcel_id, document_id)
);

CREATE TABLE lnd_number_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
