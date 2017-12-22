CREATE TABLE gw_rate_limit_policy (
    id                VARCHAR(64)   PRIMARY KEY,
    tenant_id         VARCHAR(40)   NOT NULL,
    policy_number     VARCHAR(32)   NOT NULL,
    name              VARCHAR(200)  NOT NULL,
    subject_type      VARCHAR(32)   NOT NULL,
    subject_key       VARCHAR(256)  NOT NULL,
    limit_per_window  INTEGER       NOT NULL,
    window_seconds    INTEGER       NOT NULL,
    status            VARCHAR(32)   NOT NULL,
    created_at        TIMESTAMPTZ   NOT NULL,
    activated_at      TIMESTAMPTZ,
    updated_at        TIMESTAMPTZ   NOT NULL,
    version           BIGINT        NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_gw_rlp_tenant_number ON gw_rate_limit_policy (tenant_id, policy_number);
CREATE UNIQUE INDEX uq_gw_rlp_tenant_subject ON gw_rate_limit_policy (tenant_id, subject_type, subject_key);
CREATE INDEX idx_gw_rlp_tenant_status ON gw_rate_limit_policy (tenant_id, status);

CREATE TABLE gw_rate_counter (
    policy_id     VARCHAR(64)  NOT NULL,
    window_start  TIMESTAMPTZ  NOT NULL,
    hit_count     BIGINT       NOT NULL DEFAULT 0,
    version       BIGINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (policy_id, window_start)
);

CREATE TABLE gw_resilience_policy (
    id                          VARCHAR(64)  PRIMARY KEY,
    tenant_id                   VARCHAR(40)  NOT NULL,
    policy_number               VARCHAR(32)  NOT NULL,
    dependency_name             VARCHAR(128) NOT NULL,
    failure_rate_threshold      INTEGER      NOT NULL,
    wait_duration_seconds       INTEGER      NOT NULL,
    permitted_calls_in_half_open INTEGER     NOT NULL,
    status                      VARCHAR(32)  NOT NULL,
    created_at                  TIMESTAMPTZ  NOT NULL,
    activated_at                TIMESTAMPTZ,
    updated_at                  TIMESTAMPTZ  NOT NULL,
    version                     BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_gw_rsp_tenant_number ON gw_resilience_policy (tenant_id, policy_number);
CREATE UNIQUE INDEX uq_gw_rsp_tenant_dep ON gw_resilience_policy (tenant_id, dependency_name);
CREATE INDEX idx_gw_rsp_tenant_status ON gw_resilience_policy (tenant_id, status);

CREATE TABLE gw_audit_entry (
    id             VARCHAR(64)   PRIMARY KEY,
    tenant_id      VARCHAR(40)   NOT NULL,
    actor_id       VARCHAR(128)  NOT NULL,
    action         VARCHAR(128)  NOT NULL,
    resource_type  VARCHAR(128)  NOT NULL,
    resource_id    VARCHAR(128)  NOT NULL,
    outcome        VARCHAR(32)   NOT NULL,
    detail_json    VARCHAR(4000),
    occurred_at    TIMESTAMPTZ   NOT NULL
);

CREATE INDEX idx_gw_audit_tenant_occurred ON gw_audit_entry (tenant_id, occurred_at DESC);
CREATE INDEX idx_gw_audit_resource ON gw_audit_entry (resource_type, resource_id);

CREATE TABLE gw_rate_limit_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);

CREATE TABLE gw_resilience_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
