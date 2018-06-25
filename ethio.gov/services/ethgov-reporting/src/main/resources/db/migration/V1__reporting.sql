CREATE TABLE rpt_definition (
    id                VARCHAR(64)   PRIMARY KEY,
    tenant_id         VARCHAR(40)   NOT NULL,
    report_number     VARCHAR(32)   NOT NULL,
    code              VARCHAR(64)   NOT NULL,
    name              VARCHAR(200)  NOT NULL,
    description       VARCHAR(2000),
    category          VARCHAR(32)   NOT NULL,
    format            VARCHAR(16)   NOT NULL,
    parameter_schema  TEXT          NOT NULL,
    status            VARCHAR(32)   NOT NULL,
    registered_at     TIMESTAMPTZ   NOT NULL,
    published_at      TIMESTAMPTZ,
    updated_at        TIMESTAMPTZ   NOT NULL,
    version           BIGINT        NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_rpt_def_tenant_number ON rpt_definition (tenant_id, report_number);
CREATE UNIQUE INDEX uq_rpt_def_tenant_code ON rpt_definition (tenant_id, code);
CREATE INDEX idx_rpt_def_tenant_status ON rpt_definition (tenant_id, status);
CREATE INDEX idx_rpt_def_category ON rpt_definition (category);

CREATE TABLE rpt_run (
    id                    VARCHAR(64)  PRIMARY KEY,
    tenant_id             VARCHAR(40)  NOT NULL,
    run_number            VARCHAR(32)  NOT NULL,
    report_definition_id  VARCHAR(64)  NOT NULL,
    parameters_json       TEXT         NOT NULL,
    period_start          TIMESTAMPTZ,
    period_end            TIMESTAMPTZ,
    requested_by          VARCHAR(64),
    status                VARCHAR(32)  NOT NULL,
    artifact_reference    VARCHAR(512),
    failure_reason        VARCHAR(500),
    requested_at          TIMESTAMPTZ  NOT NULL,
    started_at            TIMESTAMPTZ,
    completed_at          TIMESTAMPTZ,
    updated_at            TIMESTAMPTZ  NOT NULL,
    version               BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_rpt_run_tenant_number ON rpt_run (tenant_id, run_number);
CREATE INDEX idx_rpt_run_tenant_status ON rpt_run (tenant_id, status);
CREATE INDEX idx_rpt_run_definition ON rpt_run (report_definition_id);

CREATE TABLE rpt_definition_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);

CREATE TABLE rpt_run_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
