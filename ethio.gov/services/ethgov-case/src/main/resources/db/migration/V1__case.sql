CREATE TABLE cas_case (
    id                   VARCHAR(64)  PRIMARY KEY,
    tenant_id            VARCHAR(40)  NOT NULL,
    case_number          VARCHAR(32)  NOT NULL,
    case_type            VARCHAR(40)  NOT NULL,
    title                VARCHAR(200) NOT NULL,
    description          VARCHAR(2000),
    status               VARCHAR(32)  NOT NULL,
    priority             VARCHAR(16)  NOT NULL,
    citizen_id           VARCHAR(64),
    organization_id      VARCHAR(64),
    assignee_account_id  VARCHAR(64),
    owning_unit_id       VARCHAR(64),
    process_instance_id  VARCHAR(64),
    opened_at            TIMESTAMPTZ  NOT NULL,
    closed_at            TIMESTAMPTZ,
    updated_at           TIMESTAMPTZ  NOT NULL,
    version              BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_cas_tenant_number ON cas_case (tenant_id, case_number);
CREATE INDEX idx_cas_tenant_status ON cas_case (tenant_id, status);
CREATE INDEX idx_cas_citizen ON cas_case (citizen_id);
CREATE INDEX idx_cas_org ON cas_case (organization_id);
CREATE INDEX idx_cas_assignee ON cas_case (assignee_account_id);

CREATE TABLE cas_case_document (
    case_id     VARCHAR(64) NOT NULL REFERENCES cas_case (id) ON DELETE CASCADE,
    document_id VARCHAR(64) NOT NULL,
    PRIMARY KEY (case_id, document_id)
);

CREATE TABLE cas_case_note (
    note_id            VARCHAR(64)   PRIMARY KEY,
    case_id            VARCHAR(64)   NOT NULL REFERENCES cas_case (id) ON DELETE CASCADE,
    author_account_id  VARCHAR(64)   NOT NULL,
    body               VARCHAR(2000) NOT NULL,
    created_at         TIMESTAMPTZ   NOT NULL
);

CREATE INDEX idx_cas_note_case ON cas_case_note (case_id, created_at);

CREATE TABLE cas_number_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
