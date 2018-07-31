CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE sch_document (
    id               VARCHAR(64)   PRIMARY KEY,
    tenant_id        VARCHAR(40)   NOT NULL,
    document_number  VARCHAR(32)   NOT NULL,
    source_type      VARCHAR(32)   NOT NULL,
    source_id        VARCHAR(64)   NOT NULL,
    title            VARCHAR(300)  NOT NULL,
    summary          VARCHAR(1000),
    body_text        TEXT          NOT NULL,
    locale           VARCHAR(8)    NOT NULL,
    tags_json        VARCHAR(4000) NOT NULL,
    status           VARCHAR(32)   NOT NULL,
    indexed_at       TIMESTAMPTZ   NOT NULL,
    updated_at       TIMESTAMPTZ   NOT NULL,
    version          BIGINT        NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_sch_tenant_number ON sch_document (tenant_id, document_number);
CREATE UNIQUE INDEX uq_sch_tenant_source ON sch_document (tenant_id, source_type, source_id);
CREATE INDEX idx_sch_tenant_status ON sch_document (tenant_id, status);
CREATE INDEX idx_sch_source_type ON sch_document (source_type);
CREATE INDEX idx_sch_title_trgm ON sch_document USING gin (title gin_trgm_ops);
CREATE INDEX idx_sch_fts ON sch_document USING gin (
    to_tsvector('simple', coalesce(title,'') || ' ' || coalesce(summary,'') || ' ' || coalesce(body_text,''))
);

CREATE TABLE sch_number_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
