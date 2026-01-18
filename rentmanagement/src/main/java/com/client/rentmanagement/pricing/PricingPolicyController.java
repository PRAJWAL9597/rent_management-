package com.client.rentmanagement.pricing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pricing")
public class PricingPolicyController {

    private final PricingPolicyService service;

    public PricingPolicyController(PricingPolicyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PricingPolicy> createPolicy(@RequestBody PricingPolicy policy) {
        return ResponseEntity.ok(service.savePolicy(policy));
    }

    @GetMapping
    public ResponseEntity<List<PricingPolicy>> getAllPolicies() {
        return ResponseEntity.ok(service.getAllPolicies());
    }
}