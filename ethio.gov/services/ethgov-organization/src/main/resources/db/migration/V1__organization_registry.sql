CREATE TABLE org_organization (
    id                    VARCHAR(64)  PRIMARY KEY,
    legal_name            VARCHAR(200) NOT NULL,
    legal_name_search_key VARCHAR(200) NOT NULL,
    trade_name            VARCHAR(200),
    org_type              VARCHAR(40)  NOT NULL,
    status                VARCHAR(32)  NOT NULL,
    tin                   VARCHAR(10),
    registration_number   VARCHAR(64),
    phone_e164            VARCHAR(16)  NOT NULL,
    email                 VARCHAR(128),
    registering_tenant_id VARCHAR(40)  NOT NULL,
    owner_citizen_id      VARCHAR(64),
    established_on        DATE,
    suspension_reason     VARCHAR(500),
    region_code           VARCHAR(8)   NOT NULL,
    zone_code             VARCHAR(32)  NOT NULL,
    zone_name             VARCHAR(128) NOT NULL,
    woreda_code           VARCHAR(32)  NOT NULL,
    woreda_name           VARCHAR(128) NOT NULL,
    kebele_code           VARCHAR(32)  NOT NULL,
    kebele_name           VARCHAR(128) NOT NULL,
    locality              VARCHAR(128),
    street                VARCHAR(128),
    house_number          VARCHAR(32),
    created_at            TIMESTAMPTZ  NOT NULL,
    updated_at            TIMESTAMPTZ  NOT NULL,
    version               BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_org_organization_tin ON org_organization (tin) WHERE tin IS NOT NULL;
CREATE INDEX idx_org_organization_tenant ON org_organization (registering_tenant_id);
CREATE INDEX idx_org_organization_name ON org_organization (legal_name_search_key);
CREATE INDEX idx_org_organization_status ON org_organization (status);
CREATE INDEX idx_org_organization_owner ON org_organization (owner_citizen_id);
