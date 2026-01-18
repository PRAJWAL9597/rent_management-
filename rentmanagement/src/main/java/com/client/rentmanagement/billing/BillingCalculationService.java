package com.client.rentmanagement.billing;

import com.client.rentmanagement.billing.dto.BillingSummary;
import com.client.rentmanagement.meter.entity.MeterReading;
import com.client.rentmanagement.meter.repository.MeterReadingRepository;
import com.client.rentmanagement.pricing.PricingPolicy;
import com.client.rentmanagement.pricing.PricingPolicyRepository;
import com.client.rentmanagement.tenant.Tenant;
import com.client.rentmanagement.tenant.TenantRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class BillingCalculationService {

    private final TenantRepository tenantRepository;
    private final PricingPolicyRepository pricingPolicyRepository;
    private final MeterReadingRepository meterReadingRepository;

    public BillingCalculationService(TenantRepository tenantRepository,
                                     PricingPolicyRepository pricingPolicyRepository,
                                     MeterReadingRepository meterReadingRepository) {
        this.tenantRepository = tenantRepository;
        this.pricingPolicyRepository = pricingPolicyRepository;
        this.meterReadingRepository = meterReadingRepository;
    }

    /**
     * Updated method signature to accept dynamic common units share.
     */
    public BillingSummary calculateMonthlyBill(UUID tenantId, YearMonth billingMonth, BigDecimal commonUnitsShare) {
        // 1. Fetch and validate Tenant
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalStateException("Tenant not found"));

        if (!tenant.isActive()) {
            throw new IllegalStateException("Tenant in room " + tenant.getRoomNo() + " is inactive.");
        }

        // 2. Fetch reading for the specific month
        MeterReading reading = meterReadingRepository
                .findByTenantIdAndReadingMonth(tenantId, billingMonth.atDay(1))
                .orElseThrow(() -> new IllegalStateException("No meter reading found for " + billingMonth));

        // Guardrail: Reject negative consumption
        if (reading.getCurrentReading() < reading.getPreviousReading()) {
            throw new IllegalStateException("Negative consumption detected for Room " + tenant.getRoomNo());
        }

        // 3. Fetch the Pricing Policy valid for that month
        PricingPolicy pricing = pricingPolicyRepository
                .findTopByActiveTrueAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(billingMonth.atDay(1))
                .orElseThrow(() -> new IllegalStateException("No active pricing policy found for " + billingMonth));

        // 4. Calculate Electricity Charge (Tenant's own consumption)
        BigDecimal unitsConsumed = reading.getUnitsConsumed();
        BigDecimal electricityCharge = pricing.getUnitPrice().multiply(unitsConsumed);

        // 5. Calculate Common Area Charge using the dynamic share passed from InvoiceService
        // Calculation: (Total Common Units / Active Tenants) * Unit Price
        BigDecimal commonAreaChargePerTenant = commonUnitsShare.multiply(pricing.getUnitPrice())
                .setScale(2, RoundingMode.HALF_UP);

        // 6. Total Amount
        BigDecimal totalAmount = pricing.getRoomRent()
                .add(electricityCharge)
                .add(commonAreaChargePerTenant);

        // 7. Map to DTO
        BillingSummary summary = new BillingSummary();
        summary.setTenantId(tenantId);
        summary.setTenantName(tenant.getName());
        summary.setRoomNo(tenant.getRoomNo());
        summary.setMeterId(tenant.getMeterId());
        summary.setMonth(billingMonth);
        summary.setPreviousReading(reading.getPreviousReading());
        summary.setCurrentReading(reading.getReadingMonth().getDayOfMonth()); // Fixed reading field if needed
        summary.setCurrentReading(reading.getCurrentReading());
        summary.setUnitsConsumed(unitsConsumed.longValue());
        summary.setUnitPrice(pricing.getUnitPrice());
        summary.setElectricityCharge(electricityCharge);
        summary.setCommonAreaCharge(commonAreaChargePerTenant);
        summary.setRoomRent(pricing.getRoomRent());
        summary.setTotalAmount(totalAmount);

        return summary;
    }
}