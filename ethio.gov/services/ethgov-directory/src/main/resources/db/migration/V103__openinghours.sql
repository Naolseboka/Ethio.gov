-- Openinghours registry for openinghours
CREATE TABLE IF NOT EXISTS openinghours (
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

CREATE UNIQUE INDEX IF NOT EXISTS uk_openinghours_tenant_ref
    ON openinghours (tenant_id, reference_code);

CREATE INDEX IF NOT EXISTS idx_openinghours_tenant_status
    ON openinghours (tenant_id, status);
