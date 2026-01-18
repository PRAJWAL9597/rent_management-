package com.client.rentmanagement.billing.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingSummary {
    private UUID tenantId;
    private String tenantName;  // Added
    private String roomNo;      // Added
    private String meterId;     // Added
    private YearMonth month;

    private long previousReading; // Added
    private long currentReading;  // Added
    
    private BigDecimal roomRent;
    private BigDecimal unitPrice;
    private long unitsConsumed;
    private BigDecimal electricityCharge;
    private BigDecimal commonAreaCharge;
    private BigDecimal totalAmount;
}