package com.client.rentmanagement.tenant;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant saveTenant(Tenant tenant) {
        //  New tenants are active by default
        tenant.setActive(true);
        return tenantRepository.save(tenant);
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Tenant getTenantById(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Tenant not found with ID: " + id));
    }

    public void deleteTenant(UUID id) {
        Tenant tenant = getTenantById(id);
        tenant.setActive(false); // Soft delete: keeps history but stops new billing
        tenantRepository.save(tenant);
    }
}