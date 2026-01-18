package com.client.rentmanagement.pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate; // Added import
import java.util.Optional;
import java.util.UUID;

public interface PricingPolicyRepository extends JpaRepository<PricingPolicy, UUID> {

    // Logic: Find the policy active on a specific date
    Optional<PricingPolicy> findTopByActiveTrueAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(LocalDate date);

    Optional<PricingPolicy> findTopByActiveTrueOrderByEffectiveFromDesc();
}