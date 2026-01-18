package com.client.rentmanagement.meter.service;

import com.client.rentmanagement.meter.entity.MeterReading;
import com.client.rentmanagement.meter.repository.MeterReadingRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;

    public MeterReadingService(MeterReadingRepository meterReadingRepository) {
        this.meterReadingRepository = meterReadingRepository;
    }

    // ADDED: Logic to fetch the previous month's final reading
    public Long getLatestReadingValue(String roomNo) {
        return meterReadingRepository.findLatestByRoom(roomNo)
                .map(MeterReading::getCurrentReading)
                .orElse(0L); // Returns 0 for a brand new tenant
    }

    public MeterReading saveReading(MeterReading reading) {
        if (reading.getCurrentReading() < reading.getPreviousReading()) {
            throw new IllegalStateException("Current reading cannot be less than previous reading.");
        }
        
        long units = reading.getCurrentReading() - reading.getPreviousReading();
        reading.setUnitsConsumed(BigDecimal.valueOf(units));
        
        return meterReadingRepository.save(reading);
    }

    public List<MeterReading> getReadingsByTenant(UUID tenantId) {
        return meterReadingRepository.findByTenantId(tenantId);
    }
}