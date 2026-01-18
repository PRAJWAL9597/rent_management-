# billing_management

Production-grade **rent and electricity bill management system** for apartment owners.

This system automates monthly rent and electricity billing while keeping the database schema **fully versioned, auditable, and safe for production**.

---

## ✨ Features

Apartment owner enters **monthly meter readings** only.

The system automatically:

1. Calculates **electricity consumption**
2. Splits **common area usage** among active tenants
3. Applies **room rent, maintenance, and penalties**
4. Generates a **monthly invoice per tenant**
5. Supports **pricing rule changes over time**

---

## 🏗 Architecture

### Tech Stack

- **Spring Boot**
- **PostgreSQL**
- **Flyway (Database Versioning)**

---

## 🚀 Current Progress

- Spring Boot application running successfully
- PostgreSQL connected
- Flyway migrations validated
- GitHub repository connected and synced

---

## 🗄 Database Versioning (Flyway)

Billing systems **must not rely on auto DDL**.

This project follows strict database versioning rules:

- Database schema is defined **only via Flyway migrations**
- Migrations run automatically on application startup
- Application **will not start** if schema validation fails
- Ensures:
  - Reproducibility
  - Auditability
  - Safe production deployments

---

## 🧠 Core Domain Entities

### Tenant

Represents a person renting a room.

- Stores tenant and room details
- Has `active / inactive` status
- **Independent of billing logic**
- No pricing or calculation data stored here

---

### MeterReading

Stores **monthly electricity readings**.

- One record per tenant per month
- Contains:
  - Previous reading
  - Current reading
  - Units consumed
- **Does not calculate bills**
- Acts purely as a data source

---

### PricingPolicy

Stores **owner-controlled pricing rules**.

- Room rent
- Unit price
- Common area units
- Maintenance charges
- Effective date range
- Designed to support **pricing changes over time**
- Billing always uses the **latest active policy**

---

## 🗃 Database Schema

All tables are created and maintained using Flyway migrations.

### tenant

```sql
CREATE TABLE tenant (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    room_no VARCHAR(20) NOT NULL,
    phone_no VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    meter_id VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
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
