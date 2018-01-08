CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE gis_feature (
    id               VARCHAR(64)  PRIMARY KEY,
    tenant_id        VARCHAR(40)  NOT NULL,
    feature_number   VARCHAR(32)  NOT NULL,
    feature_type     VARCHAR(40)  NOT NULL,
    status           VARCHAR(32)  NOT NULL,
    name             VARCHAR(200) NOT NULL,
    description      VARCHAR(2000),
    wkt              TEXT         NOT NULL,
    parcel_id        VARCHAR(64),
    permit_id        VARCHAR(64),
    registered_at    TIMESTAMPTZ  NOT NULL,
    published_at     TIMESTAMPTZ,
    updated_at       TIMESTAMPTZ  NOT NULL,
    version          BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_gis_tenant_number ON gis_feature (tenant_id, feature_number);
CREATE INDEX idx_gis_tenant_status ON gis_feature (tenant_id, status);
CREATE INDEX idx_gis_type ON gis_feature (feature_type);
CREATE INDEX idx_gis_parcel ON gis_feature (parcel_id);
CREATE INDEX idx_gis_permit ON gis_feature (permit_id);

CREATE TABLE gis_number_sequence (
    tenant_id  VARCHAR(40) NOT NULL,
    seq_year   INTEGER     NOT NULL,
    next_value BIGINT      NOT NULL,
    version    BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, seq_year)
);
