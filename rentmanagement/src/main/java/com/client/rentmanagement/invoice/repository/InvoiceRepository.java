package com.client.rentmanagement.invoice.repository;

import com.client.rentmanagement.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    
    // Finds all invoices for a specific month
    List<Invoice> findByBillingMonth(LocalDate month);
    
    // Finds the full history for one tenant
    List<Invoice> findByTenantId(UUID tenantId);
    
    // Used for the safety check (one invoice per month)
    Optional<Invoice> findByTenantIdAndBillingMonth(UUID tenantId, LocalDate month);
}