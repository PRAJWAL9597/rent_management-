package com.client.rentmanagement.tenant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;
    private final TenantRepository tenantRepository; // Added this field

    // Updated constructor to include repository
    public TenantController(TenantService tenantService, TenantRepository tenantRepository) {
        this.tenantService = tenantService;
        this.tenantRepository = tenantRepository;
    }

    @PostMapping
    public ResponseEntity<Tenant> createTenant(@RequestBody Tenant tenant) {
        return ResponseEntity.ok(tenantService.saveTenant(tenant));
    }

    @GetMapping
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenant(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateTenant(@PathVariable UUID id) {
        tenantService.deleteTenant(id); // set active = false
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<Tenant>> getActiveTenants() {
        // Now tenantRepository is accessible
        return ResponseEntity.ok(tenantRepository.findByActiveTrue());
    }

    @GetMapping("/room/{roomNo}")
    public ResponseEntity<Tenant> getByRoom(@PathVariable String roomNo) {
        // Now tenantRepository is accessible
        return tenantRepository.findByRoomNoAndActiveTrue(roomNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}