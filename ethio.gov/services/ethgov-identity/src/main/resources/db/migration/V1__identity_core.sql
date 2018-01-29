-- Identity Platform schema (database-per-service)

CREATE TABLE id_user_account (
    id                     VARCHAR(64)  PRIMARY KEY,
    username               VARCHAR(128) NOT NULL,
    account_type           VARCHAR(32)  NOT NULL,
    status                 VARCHAR(32)  NOT NULL,
    password_hash          VARCHAR(100),
    failed_login_attempts  INTEGER      NOT NULL DEFAULT 0,
    locked_until           TIMESTAMPTZ,
    created_at             TIMESTAMPTZ  NOT NULL,
    updated_at             TIMESTAMPTZ  NOT NULL,
    version                BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_id_user_account_username ON id_user_account (username);
CREATE INDEX idx_id_user_account_status ON id_user_account (status);

CREATE TABLE id_tenant_membership (
    id               BIGSERIAL PRIMARY KEY,
    account_id       VARCHAR(64)  NOT NULL REFERENCES id_user_account (id) ON DELETE CASCADE,
    tenant_id        VARCHAR(40)  NOT NULL,
    roles_csv        VARCHAR(2000) NOT NULL,
    permissions_csv  VARCHAR(4000) NOT NULL,
    CONSTRAINT uq_membership_account_tenant UNIQUE (account_id, tenant_id)
);

CREATE INDEX idx_id_membership_tenant ON id_tenant_membership (tenant_id);

CREATE TABLE id_oauth_client (
    id                  VARCHAR(64)  PRIMARY KEY,
    client_id           VARCHAR(100) NOT NULL,
    client_secret_hash  VARCHAR(100) NOT NULL,
    tenant_id           VARCHAR(40)  NOT NULL,
    redirect_uris_csv   VARCHAR(2000) NOT NULL,
    grant_types_csv     VARCHAR(500) NOT NULL,
    scopes_csv          VARCHAR(1000) NOT NULL,
    confidential        BOOLEAN      NOT NULL,
    active              BOOLEAN      NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL
);

CREATE UNIQUE INDEX uq_id_oauth_client_client_id ON id_oauth_client (client_id);
CREATE INDEX idx_id_oauth_client_tenant ON id_oauth_client (tenant_id);
