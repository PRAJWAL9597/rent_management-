package com.client.rentmanagement.invoice.entity;

import com.client.rentmanagement.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "invoice",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_invoice_tenant_month",
                        columnNames = {"tenant_id", "billing_month"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Invoice extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "tenant_name")
    private String tenantName;

    @Column(name = "room_no")
    private String roomNo;

    @Column(name = "billing_month", nullable = false)
    private LocalDate billingMonth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.GENERATED;

    @Column(name = "room_rent", nullable = false, precision = 10, scale = 2)
    private BigDecimal roomRent;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "units_consumed", nullable = false)
    private long unitsConsumed;

    @Column(name = "electricity_charge", nullable = false, precision = 10, scale = 2)
    private BigDecimal electricityCharge;

    @Column(name = "common_area_charge", nullable = false, precision = 10, scale = 2)
    private BigDecimal commonAreaCharge;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    // Inside Invoice.java
    @Column(name = "previous_reading")
    private long previousReading;

    @Column(name = "current_reading")
    private long currentReading;
}