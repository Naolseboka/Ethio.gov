CREATE TABLE ods_dataset (
    id                 VARCHAR(64)   PRIMARY KEY,
    tenant_id          VARCHAR(40)   NOT NULL,
    dataset_number     VARCHAR(32)   NOT NULL,
    slug               VARCHAR(80)   NOT NULL,
    title              VARCHAR(200)  NOT NULL,
    description        VARCHAR(4000) NOT NULL,
    theme              VARCHAR(32)   NOT NULL,
    license            VARCHAR(32)   NOT NULL,
    publisher_org_id   VARCHAR(64),
    contact_email      VARCHAR(254),
    locale             VARCHAR(8)    NOT NULL,
    status             VARCHAR(32)   NOT NULL,
    registered_at      TIMESTAMPTZ   NOT NULL,
    published_at       TIMESTAMPTZ,
    updated_at         TIMESTAMPTZ   NOT NULL,
    version            BIGINT        NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_ods_tenant_number ON ods_dataset (tenant_id, dataset_number);
CREATE UNIQUE INDEX uq_ods_tenant_slug ON ods_dataset (tenant_id, slug);
CREATE INDEX idx_ods_tenant_status ON ods_dataset (tenant_id, status);
CREATE INDEX idx_ods_theme ON ods_dataset (theme);

CREATE TABLE ods_resource (
    id               VARCHAR(64)   PRIMARY KEY,
    tenant_id        VARCHAR(40)   NOT NULL,
    resource_number  VARCHAR(32)   NOT NULL,
    dataset_id       VARCHAR(64)   NOT NULL,
    title            VARCHAR(200)  NOT NULL,
    description      VARCHAR(2000),
    format           VARCHAR(16)   NOT NULL,
    access_url       VARCHAR(1000),
    document_id      VARCHAR(64),
    media_type       VARCHAR(120),
    status           VARCHAR(32)   NOT NULL,
    added_at         TIMESTAMPTZ   NOT NULL,
    updated_at       TIMESTAMPTZ   NOT NULL,
    version          BIGINT        NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_odr_tenant_number ON ods_resource (tenant_id, resource_number);
CREATE INDEX idx_odr_dataset ON ods_resource (dataset_id);
CREATE INDEX idx_odr_tenant_status ON ods_resource (tenant_id, status);

CREATE TABLE ods_dataset_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);

CREATE TABLE ods_resource_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
