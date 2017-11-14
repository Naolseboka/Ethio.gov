CREATE TABLE dir_organizational_unit (
    id                     VARCHAR(64)  PRIMARY KEY,
    tenant_id              VARCHAR(40)  NOT NULL,
    code                   VARCHAR(32)  NOT NULL,
    name                   VARCHAR(200) NOT NULL,
    name_search_key        VARCHAR(200) NOT NULL,
    unit_type              VARCHAR(32)  NOT NULL,
    status                 VARCHAR(32)  NOT NULL,
    parent_id              VARCHAR(64),
    linked_organization_id VARCHAR(64),
    description            VARCHAR(500),
    created_at             TIMESTAMPTZ  NOT NULL,
    updated_at             TIMESTAMPTZ  NOT NULL,
    version                BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT fk_dir_ou_parent FOREIGN KEY (parent_id) REFERENCES dir_organizational_unit (id)
);

CREATE UNIQUE INDEX uq_dir_ou_tenant_code ON dir_organizational_unit (tenant_id, code);
CREATE INDEX idx_dir_ou_tenant_status ON dir_organizational_unit (tenant_id, status);
CREATE INDEX idx_dir_ou_parent ON dir_organizational_unit (parent_id);
CREATE INDEX idx_dir_ou_name ON dir_organizational_unit (name_search_key);

CREATE TABLE dir_public_employee (
    id                     VARCHAR(64)  PRIMARY KEY,
    tenant_id              VARCHAR(40)  NOT NULL,
    employee_number        VARCHAR(32)  NOT NULL,
    citizen_id             VARCHAR(64)  NOT NULL,
    display_name           VARCHAR(200) NOT NULL,
    organizational_unit_id VARCHAR(64)  NOT NULL,
    job_title              VARCHAR(120) NOT NULL,
    identity_account_id    VARCHAR(64),
    status                 VARCHAR(32)  NOT NULL,
    hired_on               DATE         NOT NULL,
    separated_on           DATE,
    created_at             TIMESTAMPTZ  NOT NULL,
    updated_at             TIMESTAMPTZ  NOT NULL,
    version                BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT fk_dir_emp_ou FOREIGN KEY (organizational_unit_id) REFERENCES dir_organizational_unit (id)
);

CREATE UNIQUE INDEX uq_dir_emp_tenant_number ON dir_public_employee (tenant_id, employee_number);
CREATE UNIQUE INDEX uq_dir_emp_identity ON dir_public_employee (identity_account_id)
    WHERE identity_account_id IS NOT NULL;
CREATE INDEX idx_dir_emp_tenant_status ON dir_public_employee (tenant_id, status);
CREATE INDEX idx_dir_emp_unit ON dir_public_employee (organizational_unit_id);
CREATE INDEX idx_dir_emp_citizen ON dir_public_employee (citizen_id);
