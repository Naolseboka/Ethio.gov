-- Rolebinding registry for rolebinding
CREATE TABLE IF NOT EXISTS rolebinding (
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

CREATE UNIQUE INDEX IF NOT EXISTS uk_rolebinding_tenant_ref
    ON rolebinding (tenant_id, reference_code);

CREATE INDEX IF NOT EXISTS idx_rolebinding_tenant_status
    ON rolebinding (tenant_id, status);
