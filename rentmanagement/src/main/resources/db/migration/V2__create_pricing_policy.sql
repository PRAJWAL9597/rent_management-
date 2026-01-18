CREATE TABLE pricing_policy (
    id UUID PRIMARY KEY,
    room_rent NUMERIC(10,2) NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    common_area_unit NUMERIC(10,2) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
