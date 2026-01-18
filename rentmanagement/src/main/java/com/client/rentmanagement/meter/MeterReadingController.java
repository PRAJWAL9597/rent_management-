package com.client.rentmanagement.meter;

import com.client.rentmanagement.meter.entity.MeterReading;
import com.client.rentmanagement.meter.service.MeterReadingService;
import com.client.rentmanagement.meter.repository.MeterReadingRepository; // Added import
import com.client.rentmanagement.tenant.Tenant;
import com.client.rentmanagement.tenant.TenantRepository;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List; // Added import
//import java.util.UUID;

@RestController
@RequestMapping("/api/meter-readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;
    private final TenantRepository tenantRepository;
    private final MeterReadingRepository meterReadingRepository; // Added this field

    public MeterReadingController(MeterReadingService meterReadingService, 
                                  TenantRepository tenantRepository, 
                                  MeterReadingRepository meterReadingRepository) { // Updated constructor
        this.meterReadingService = meterReadingService;
        this.tenantRepository = tenantRepository;
        this.meterReadingRepository = meterReadingRepository;
    }

    @PostMapping("/room/{roomNo}")
    public ResponseEntity<MeterReading> addReadingByRoom(@PathVariable String roomNo, @RequestBody MeterReading reading) {
        Tenant tenant = tenantRepository.findByRoomNoAndActiveTrue(roomNo)
                .orElseThrow(() -> new IllegalStateException("No active tenant found in room: " + roomNo));
        
        reading.setTenantId(tenant.getId());
        return ResponseEntity.ok(meterReadingService.saveReading(reading));
    }

    @GetMapping("/month/{month}")
    public ResponseEntity<List<MeterReading>> getReadingsByMonth(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        
        // Now meterReadingRepository is accessible
        List<MeterReading> readings = meterReadingRepository.findAll().stream()
                .filter(r -> YearMonth.from(r.getReadingMonth()).equals(month))
                .toList();
                
        return ResponseEntity.ok(readings);
    }
    @GetMapping("/latest/{roomNo}")
public ResponseEntity<Long> getLatestReading(@PathVariable String roomNo) {
    return ResponseEntity.ok(meterReadingService.getLatestReadingValue(roomNo));
}
}