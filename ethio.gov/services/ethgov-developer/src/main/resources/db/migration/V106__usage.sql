-- Usage registry for usage
CREATE TABLE IF NOT EXISTS usage (
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

CREATE UNIQUE INDEX IF NOT EXISTS uk_usage_tenant_ref
    ON usage (tenant_id, reference_code);

CREATE INDEX IF NOT EXISTS idx_usage_tenant_status
    ON usage (tenant_id, status);
