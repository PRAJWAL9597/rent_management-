CREATE TABLE invoice (
    id UUID PRIMARY KEY,

    tenant_id UUID NOT NULL,
    billing_month DATE NOT NULL,

    room_rent NUMERIC(10,2) NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,

    units_consumed BIGINT NOT NULL,
    electricity_charge NUMERIC(10,2) NOT NULL,
    common_area_charge NUMERIC(10,2) NOT NULL,

    total_amount NUMERIC(10,2) NOT NULL,

    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_invoice_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenant(id),

    CONSTRAINT uq_invoice_tenant_month
        UNIQUE (tenant_id, billing_month)
);
