CREATE TABLE anl_metric (
    id              VARCHAR(64)   PRIMARY KEY,
    tenant_id       VARCHAR(40)   NOT NULL,
    metric_number   VARCHAR(32)   NOT NULL,
    code            VARCHAR(64)   NOT NULL,
    name            VARCHAR(200)  NOT NULL,
    description     VARCHAR(2000),
    category        VARCHAR(32)   NOT NULL,
    unit            VARCHAR(16)   NOT NULL,
    aggregation     VARCHAR(16)   NOT NULL,
    status          VARCHAR(32)   NOT NULL,
    registered_at   TIMESTAMPTZ   NOT NULL,
    published_at    TIMESTAMPTZ,
    updated_at      TIMESTAMPTZ   NOT NULL,
    version         BIGINT        NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_anl_metric_tenant_number ON anl_metric (tenant_id, metric_number);
CREATE UNIQUE INDEX uq_anl_metric_tenant_code ON anl_metric (tenant_id, code);
CREATE INDEX idx_anl_metric_tenant_status ON anl_metric (tenant_id, status);
CREATE INDEX idx_anl_metric_category ON anl_metric (category);

CREATE TABLE anl_observation (
    id                    VARCHAR(64)    PRIMARY KEY,
    tenant_id             VARCHAR(40)    NOT NULL,
    observation_number    VARCHAR(32)    NOT NULL,
    metric_definition_id  VARCHAR(64)    NOT NULL,
    metric_value          NUMERIC(18, 6) NOT NULL,
    period_start          TIMESTAMPTZ    NOT NULL,
    period_end            TIMESTAMPTZ    NOT NULL,
    dimensions_json       VARCHAR(4000)  NOT NULL,
    source_reference      VARCHAR(64),
    status                VARCHAR(32)    NOT NULL,
    void_reason           VARCHAR(500),
    recorded_at           TIMESTAMPTZ    NOT NULL,
    updated_at            TIMESTAMPTZ    NOT NULL,
    version               BIGINT         NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_anl_obs_tenant_number ON anl_observation (tenant_id, observation_number);
CREATE INDEX idx_anl_obs_metric ON anl_observation (metric_definition_id);
CREATE INDEX idx_anl_obs_tenant_status ON anl_observation (tenant_id, status);
CREATE INDEX idx_anl_obs_period ON anl_observation (period_start, period_end);

CREATE TABLE anl_metric_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);

CREATE TABLE anl_observation_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
