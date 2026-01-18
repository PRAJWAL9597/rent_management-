package com.client.rentmanagement.pricing;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PricingPolicyService {

    private final PricingPolicyRepository repository;

    public PricingPolicyService(PricingPolicyRepository repository) {
        this.repository = repository;
    }

    public PricingPolicy savePolicy(PricingPolicy policy) {
        // Business Rule: If this is active, we should technically deactivate others, 
        // but for now, we'll just save it as the latest.
        policy.setActive(true);
        return repository.save(policy);
    }

    public List<PricingPolicy> getAllPolicies() {
        return repository.findAll();
    }

    public PricingPolicy getLatestPolicy() {
        return repository.findTopByActiveTrueOrderByEffectiveFromDesc()
                .orElseThrow(() -> new IllegalStateException("No active pricing policy found. Please create one first."));
    }
}