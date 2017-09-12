-- Evidence registry for evidence
CREATE TABLE IF NOT EXISTS evidence (
    id UUID PRIMARY KEY,
    reference_code VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    tenant_id VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_evidence_tenant_ref
    ON evidence (tenant_id, reference_code);

CREATE INDEX IF NOT EXISTS idx_evidence_tenant_status
    ON evidence (tenant_id, status);
