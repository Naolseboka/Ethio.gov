CREATE TABLE cit_citizen (
    id                    VARCHAR(64)  PRIMARY KEY,
    given_name            VARCHAR(80)  NOT NULL,
    father_name           VARCHAR(80)  NOT NULL,
    grandfather_name      VARCHAR(80),
    name_search_key       VARCHAR(260) NOT NULL,
    date_of_birth         DATE         NOT NULL,
    sex                   VARCHAR(16)  NOT NULL,
    phone_e164            VARCHAR(16)  NOT NULL,
    fayda_id              VARCHAR(16),
    identity_account_id   VARCHAR(64),
    registering_tenant_id VARCHAR(40)  NOT NULL,
    status                VARCHAR(32)  NOT NULL,
    deceased_on           DATE,
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

CREATE UNIQUE INDEX uq_cit_citizen_phone ON cit_citizen (phone_e164);
CREATE UNIQUE INDEX uq_cit_citizen_fayda ON cit_citizen (fayda_id) WHERE fayda_id IS NOT NULL;
CREATE INDEX idx_cit_citizen_tenant ON cit_citizen (registering_tenant_id);
CREATE INDEX idx_cit_citizen_name_search ON cit_citizen (name_search_key);
CREATE INDEX idx_cit_citizen_status ON cit_citizen (status);
