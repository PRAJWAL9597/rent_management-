package com.client.rentmanagement.pricing;

import com.client.rentmanagement.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pricing_policy")
@Getter
@Setter
public class PricingPolicy extends BaseEntity { // Inherits id, active, createdAt, updatedAt

    @Column(name = "room_rent", nullable = false, precision = 10, scale = 2)
    private BigDecimal roomRent;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    // Change: Set nullable to true because common area units are now dynamic
    @Column(name = "common_area_unit", nullable = true) 
    private BigDecimal commonAreaUnit;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

}