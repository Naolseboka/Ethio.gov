-- Guardianship registry for guardianship
CREATE TABLE IF NOT EXISTS guardianship (
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

CREATE UNIQUE INDEX IF NOT EXISTS uk_guardianship_tenant_ref
    ON guardianship (tenant_id, reference_code);

CREATE INDEX IF NOT EXISTS idx_guardianship_tenant_status
    ON guardianship (tenant_id, status);
