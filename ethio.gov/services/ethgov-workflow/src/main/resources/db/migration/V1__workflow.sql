CREATE TABLE wf_definition (
    id                 VARCHAR(64)  PRIMARY KEY,
    tenant_id          VARCHAR(40)  NOT NULL,
    definition_key     VARCHAR(64)  NOT NULL,
    name               VARCHAR(200) NOT NULL,
    description        VARCHAR(500),
    definition_version INTEGER      NOT NULL,
    status             VARCHAR(32)  NOT NULL,
    created_at         TIMESTAMPTZ  NOT NULL,
    updated_at         TIMESTAMPTZ  NOT NULL,
    version            BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_wf_definition_tenant_key ON wf_definition (tenant_id, definition_key);
CREATE INDEX idx_wf_definition_tenant_status ON wf_definition (tenant_id, status);

CREATE TABLE wf_definition_step (
    id            BIGSERIAL PRIMARY KEY,
    definition_id VARCHAR(64)  NOT NULL REFERENCES wf_definition (id) ON DELETE CASCADE,
    step_key      VARCHAR(32)  NOT NULL,
    name          VARCHAR(120) NOT NULL,
    step_type     VARCHAR(32)  NOT NULL,
    assignee_role VARCHAR(64),
    ordinal       INTEGER      NOT NULL
);

CREATE UNIQUE INDEX uq_wf_step_def_key ON wf_definition_step (definition_id, step_key);
CREATE UNIQUE INDEX uq_wf_step_def_ord ON wf_definition_step (definition_id, ordinal);

CREATE TABLE wf_process_instance (
    id                     VARCHAR(64)  PRIMARY KEY,
    tenant_id              VARCHAR(40)  NOT NULL,
    definition_id          VARCHAR(64)  NOT NULL REFERENCES wf_definition (id),
    business_key           VARCHAR(128) NOT NULL,
    started_by_account_id  VARCHAR(64)  NOT NULL,
    status                 VARCHAR(32)  NOT NULL,
    current_step_ordinal   INTEGER      NOT NULL,
    current_step_key       VARCHAR(32)  NOT NULL,
    cancellation_reason    VARCHAR(500),
    started_at             TIMESTAMPTZ  NOT NULL,
    completed_at           TIMESTAMPTZ,
    updated_at             TIMESTAMPTZ  NOT NULL,
    version                BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_wf_process_tenant_biz ON wf_process_instance (tenant_id, business_key);
CREATE INDEX idx_wf_process_tenant_status ON wf_process_instance (tenant_id, status);
CREATE INDEX idx_wf_process_definition ON wf_process_instance (definition_id);

CREATE TABLE wf_task (
    id                     VARCHAR(64)  PRIMARY KEY,
    tenant_id              VARCHAR(40)  NOT NULL,
    process_instance_id    VARCHAR(64)  NOT NULL REFERENCES wf_process_instance (id),
    step_key               VARCHAR(32)  NOT NULL,
    name                   VARCHAR(120) NOT NULL,
    assignee_role          VARCHAR(64),
    status                 VARCHAR(32)  NOT NULL,
    claimed_by_account_id  VARCHAR(64),
    outcome                VARCHAR(64),
    created_at             TIMESTAMPTZ  NOT NULL,
    completed_at           TIMESTAMPTZ,
    updated_at             TIMESTAMPTZ  NOT NULL,
    version                BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX idx_wf_task_process ON wf_task (process_instance_id, status);
CREATE INDEX idx_wf_task_claimant ON wf_task (claimed_by_account_id);
CREATE INDEX idx_wf_task_tenant_status ON wf_task (tenant_id, status);
