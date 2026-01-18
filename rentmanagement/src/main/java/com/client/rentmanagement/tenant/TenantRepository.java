package com.client.rentmanagement.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Added import
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository // Added annotation
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByRoomNoAndActiveTrue(String roomNo);
    List<Tenant> findByActiveTrue();
}