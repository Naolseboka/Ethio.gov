CREATE TABLE prm_permit (
    id                    VARCHAR(64)    PRIMARY KEY,
    tenant_id             VARCHAR(40)    NOT NULL,
    permit_number         VARCHAR(32)    NOT NULL,
    permit_type           VARCHAR(40)    NOT NULL,
    structure_use         VARCHAR(32)    NOT NULL,
    project_name          VARCHAR(200)   NOT NULL,
    description           VARCHAR(2000),
    status                VARCHAR(32)    NOT NULL,
    citizen_id            VARCHAR(64),
    organization_id       VARCHAR(64),
    parcel_id             VARCHAR(64)    NOT NULL,
    floors                INTEGER        NOT NULL,
    floor_area_sq_m       NUMERIC(14, 2) NOT NULL,
    estimated_cost_etb    NUMERIC(18, 2),
    region                VARCHAR(120)   NOT NULL,
    zone                  VARCHAR(120),
    woreda                VARCHAR(120)   NOT NULL,
    kebele                VARCHAR(120),
    locality              VARCHAR(200),
    linked_case_id        VARCHAR(64),
    process_instance_id   VARCHAR(64),
    valid_from            TIMESTAMPTZ,
    valid_to              TIMESTAMPTZ,
    issued_at             TIMESTAMPTZ,
    completed_at          TIMESTAMPTZ,
    rejection_reason      VARCHAR(500),
    suspension_reason     VARCHAR(500),
    revocation_reason     VARCHAR(500),
    applied_at            TIMESTAMPTZ    NOT NULL,
    updated_at            TIMESTAMPTZ    NOT NULL,
    version               BIGINT         NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_prm_tenant_number ON prm_permit (tenant_id, permit_number);
CREATE INDEX idx_prm_tenant_status ON prm_permit (tenant_id, status);
CREATE INDEX idx_prm_parcel ON prm_permit (parcel_id);
CREATE INDEX idx_prm_org ON prm_permit (organization_id);
CREATE INDEX idx_prm_citizen ON prm_permit (citizen_id);
CREATE INDEX idx_prm_case ON prm_permit (linked_case_id);

CREATE TABLE prm_permit_document (
    permit_id   VARCHAR(64) NOT NULL REFERENCES prm_permit (id) ON DELETE CASCADE,
    document_id VARCHAR(64) NOT NULL,
    PRIMARY KEY (permit_id, document_id)
);

CREATE TABLE prm_number_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
