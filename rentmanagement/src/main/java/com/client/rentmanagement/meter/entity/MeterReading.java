package com.client.rentmanagement.meter.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "meter_reading",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_tenant_month", columnNames = {"tenant_id", "reading_month"})
    }
)
@Getter
@Setter
public class MeterReading {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "reading_month", nullable = false)
    private LocalDate readingMonth;

    @Column(name = "previous_reading", nullable = false)
    private long previousReading;

    @Column(name = "current_reading", nullable = false)
    private long currentReading;

    @Column(name = "units_consumed", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitsConsumed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * This logic runs automatically before the record is inserted into the database.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        
        // AUTOMATION: If the owner forgot to provide a reading month, 
        // default it to the first day of the current month.
        if (this.readingMonth == null) {
            this.readingMonth = LocalDate.now().withDayOfMonth(1);
        }

        // AUTOMATION: Calculate units consumed automatically (Current - Previous).
        this.unitsConsumed = BigDecimal.valueOf(this.currentReading - this.previousReading);
    }
}