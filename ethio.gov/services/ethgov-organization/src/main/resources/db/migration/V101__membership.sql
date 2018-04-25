-- Membership registry for membership
CREATE TABLE IF NOT EXISTS membership (
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

CREATE UNIQUE INDEX IF NOT EXISTS uk_membership_tenant_ref
    ON membership (tenant_id, reference_code);

CREATE INDEX IF NOT EXISTS idx_membership_tenant_status
    ON membership (tenant_id, status);
