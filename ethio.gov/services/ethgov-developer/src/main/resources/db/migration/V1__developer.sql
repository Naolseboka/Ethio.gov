CREATE TABLE dev_app (
    id              VARCHAR(64)   PRIMARY KEY,
    tenant_id       VARCHAR(40)   NOT NULL,
    app_number      VARCHAR(32)   NOT NULL,
    slug            VARCHAR(64)   NOT NULL,
    name            VARCHAR(200)  NOT NULL,
    description     VARCHAR(2000),
    owner_org_id    VARCHAR(64),
    contact_email   VARCHAR(254)  NOT NULL,
    allowed_scopes  VARCHAR(4000) NOT NULL,
    status          VARCHAR(32)   NOT NULL,
    registered_at   TIMESTAMPTZ   NOT NULL,
    activated_at    TIMESTAMPTZ,
    updated_at      TIMESTAMPTZ   NOT NULL,
    version         BIGINT        NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_dev_app_tenant_number ON dev_app (tenant_id, app_number);
CREATE UNIQUE INDEX uq_dev_app_tenant_slug ON dev_app (tenant_id, slug);
CREATE INDEX idx_dev_app_tenant_status ON dev_app (tenant_id, status);

CREATE TABLE dev_credential (
    id                 VARCHAR(64)  PRIMARY KEY,
    tenant_id          VARCHAR(40)  NOT NULL,
    credential_number  VARCHAR(32)  NOT NULL,
    developer_app_id   VARCHAR(64)  NOT NULL,
    key_prefix         VARCHAR(32)  NOT NULL,
    key_hash           VARCHAR(64)  NOT NULL,
    label              VARCHAR(120) NOT NULL,
    expires_at         TIMESTAMPTZ,
    status             VARCHAR(32)  NOT NULL,
    issued_at          TIMESTAMPTZ  NOT NULL,
    revoked_at         TIMESTAMPTZ,
    updated_at         TIMESTAMPTZ  NOT NULL,
    version            BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_dev_cred_tenant_number ON dev_credential (tenant_id, credential_number);
CREATE UNIQUE INDEX uq_dev_cred_key_hash ON dev_credential (key_hash);
CREATE INDEX idx_dev_cred_app ON dev_credential (developer_app_id);
CREATE INDEX idx_dev_cred_tenant_status ON dev_credential (tenant_id, status);

CREATE TABLE dev_app_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);

CREATE TABLE dev_credential_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
