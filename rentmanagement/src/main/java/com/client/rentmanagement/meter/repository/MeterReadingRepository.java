package com.client.rentmanagement.meter.repository;

import com.client.rentmanagement.meter.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, UUID> {
    
    Optional<MeterReading> findByTenantIdAndReadingMonth(UUID tenantId, LocalDate readingMonth);

    List<MeterReading> findByTenantId(UUID tenantId);

    // Uses a custom JPQL query to join with Tenant via tenantId
    @Query("SELECT m FROM MeterReading m JOIN Tenant t ON m.tenantId = t.id " +
           "WHERE t.roomNo = :roomNo ORDER BY m.readingMonth DESC")
    List<MeterReading> findLastReadingsByRoom(@Param("roomNo") String roomNo);

    // Helper to get the single latest record
    default Optional<MeterReading> findLatestByRoom(String roomNo) {
        return findLastReadingsByRoom(roomNo).stream().findFirst();
    }
}