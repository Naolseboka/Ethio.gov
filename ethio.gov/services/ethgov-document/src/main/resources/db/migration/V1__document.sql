CREATE TABLE doc_document (
    id                     VARCHAR(64)  PRIMARY KEY,
    tenant_id              VARCHAR(40)  NOT NULL,
    title                  VARCHAR(200) NOT NULL,
    description            VARCHAR(1000),
    classification         VARCHAR(32)  NOT NULL,
    status                 VARCHAR(32)  NOT NULL,
    content_type           VARCHAR(128) NOT NULL,
    original_file_name     VARCHAR(255) NOT NULL,
    storage_object_key     VARCHAR(512) NOT NULL,
    doc_version            INTEGER      NOT NULL,
    size_bytes             BIGINT,
    content_hash           VARCHAR(64),
    owner_type             VARCHAR(32),
    owner_id               VARCHAR(64),
    uploaded_by_account_id VARCHAR(64),
    created_at             TIMESTAMPTZ  NOT NULL,
    updated_at             TIMESTAMPTZ  NOT NULL,
    version                BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_doc_storage_key ON doc_document (storage_object_key);
CREATE INDEX idx_doc_tenant_status ON doc_document (tenant_id, status);
CREATE INDEX idx_doc_owner ON doc_document (owner_type, owner_id);
CREATE INDEX idx_doc_content_hash ON doc_document (content_hash) WHERE content_hash IS NOT NULL;
