CREATE TABLE lic_license (
    id                    VARCHAR(64)   PRIMARY KEY,
    tenant_id             VARCHAR(40)   NOT NULL,
    license_number        VARCHAR(32)   NOT NULL,
    license_type          VARCHAR(40)   NOT NULL,
    business_name         VARCHAR(200)  NOT NULL,
    activity_description  VARCHAR(2000),
    status                VARCHAR(32)   NOT NULL,
    organization_id       VARCHAR(64)   NOT NULL,
    citizen_id            VARCHAR(64),
    region                VARCHAR(120)  NOT NULL,
    zone                  VARCHAR(120),
    woreda                VARCHAR(120)  NOT NULL,
    kebele                VARCHAR(120),
    locality              VARCHAR(200),
    parcel_id             VARCHAR(64),
    linked_case_id        VARCHAR(64),
    process_instance_id   VARCHAR(64),
    valid_from            TIMESTAMPTZ,
    valid_to              TIMESTAMPTZ,
    issued_at             TIMESTAMPTZ,
    rejection_reason      VARCHAR(500),
    suspension_reason     VARCHAR(500),
    revocation_reason     VARCHAR(500),
    applied_at            TIMESTAMPTZ   NOT NULL,
    updated_at            TIMESTAMPTZ   NOT NULL,
    version               BIGINT        NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_lic_tenant_number ON lic_license (tenant_id, license_number);
CREATE INDEX idx_lic_tenant_status ON lic_license (tenant_id, status);
CREATE INDEX idx_lic_org ON lic_license (organization_id);
CREATE INDEX idx_lic_citizen ON lic_license (citizen_id);
CREATE INDEX idx_lic_case ON lic_license (linked_case_id);
CREATE INDEX idx_lic_parcel ON lic_license (parcel_id);

CREATE TABLE lic_license_document (
    license_id  VARCHAR(64) NOT NULL REFERENCES lic_license (id) ON DELETE CASCADE,
    document_id VARCHAR(64) NOT NULL,
    PRIMARY KEY (license_id, document_id)
);

CREATE TABLE lic_number_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
