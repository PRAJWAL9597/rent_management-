CREATE TABLE meter_reading (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    reading_month DATE NOT NULL,
    previous_reading BIGINT NOT NULL,
    current_reading BIGINT NOT NULL,
    units_consumed NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_meter_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenant(id),

    CONSTRAINT uq_tenant_month
        UNIQUE (tenant_id, reading_month)
);
