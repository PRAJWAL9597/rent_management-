package com.client.rentmanagement.invoice;

import com.client.rentmanagement.billing.BillingCalculationService;
import com.client.rentmanagement.billing.dto.BillingSummary;
import com.client.rentmanagement.invoice.entity.Invoice;
import com.client.rentmanagement.invoice.entity.InvoiceStatus;
import com.client.rentmanagement.invoice.repository.InvoiceRepository;
import com.client.rentmanagement.invoice.service.PdfService; // Added import
import com.client.rentmanagement.tenant.Tenant;
import com.client.rentmanagement.tenant.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream; // Added import
import java.io.IOException;           // Added import
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;        // Added import
import java.util.zip.ZipOutputStream; // Added import

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final TenantRepository tenantRepository;
    private final BillingCalculationService billingCalculationService;
    private final PdfService pdfService; // Added dependency

    public InvoiceService(InvoiceRepository invoiceRepository,
                          TenantRepository tenantRepository,
                          BillingCalculationService billingCalculationService,
                          PdfService pdfService) { // Added to constructor
        this.invoiceRepository = invoiceRepository;
        this.tenantRepository = tenantRepository;
        this.billingCalculationService = billingCalculationService;
        this.pdfService = pdfService;
    }

    public Invoice getInvoiceById(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));
    }

    @Transactional
    public String generateBulkInvoices(YearMonth billingMonth, int totalCommonUnits) {
        List<Tenant> activeTenants = tenantRepository.findByActiveTrue();

        if (activeTenants.isEmpty()) {
            return "No active tenants found for billing.";
        }

        BigDecimal commonUnitsPerTenant = BigDecimal.valueOf(totalCommonUnits)
                .divide(BigDecimal.valueOf(activeTenants.size()), 2, RoundingMode.HALF_UP);

        int success = 0;
        int failed = 0;

        for (Tenant tenant : activeTenants) {
            try {
                generateInvoice(tenant.getId(), billingMonth, commonUnitsPerTenant);
                success++;
            } catch (Exception e) {
                failed++;
            }
        }
        return String.format("Bulk generation complete: %d success, %d skipped.", success, failed);
    }

    @Transactional
    public Invoice generateInvoice(UUID tenantId, YearMonth billingMonth, BigDecimal commonUnitsShare) {
        LocalDate firstDayOfBillingMonth = billingMonth.atDay(1);

        Optional<Invoice> existing = invoiceRepository.findByTenantIdAndBillingMonth(tenantId, firstDayOfBillingMonth);
        if (existing.isPresent()) {
            return existing.get();
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        BillingSummary summary = billingCalculationService.calculateMonthlyBill(tenantId, billingMonth, commonUnitsShare);

        Invoice invoice = new Invoice();
        invoice.setTenantId(tenantId);
        invoice.setTenantName(tenant.getName());
        invoice.setRoomNo(tenant.getRoomNo());
        invoice.setBillingMonth(firstDayOfBillingMonth);
        invoice.setStatus(InvoiceStatus.GENERATED);

        invoice.setRoomRent(summary.getRoomRent());
        invoice.setUnitPrice(summary.getUnitPrice());
        invoice.setUnitsConsumed(summary.getUnitsConsumed());
        invoice.setElectricityCharge(summary.getElectricityCharge());
        invoice.setCommonAreaCharge(summary.getCommonAreaCharge());
        invoice.setPreviousReading(summary.getPreviousReading());
        invoice.setCurrentReading(summary.getCurrentReading());
        invoice.setTotalAmount(summary.getTotalAmount());

        return invoiceRepository.save(invoice);
    }

    /**
     * ADDED: Logic to loop through invoices and create a ZIP file
     */
    public byte[] generateInvoicesZip(YearMonth month) {
        List<Invoice> invoices = invoiceRepository.findByBillingMonth(month.atDay(1));
        
        if (invoices.isEmpty()) {
            throw new RuntimeException("No invoices found for " + month);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Invoice inv : invoices) {
                byte[] pdf = pdfService.generateInvoicePdf(inv);
                
                // Set file name as Invoice_Room_101.pdf
                ZipEntry entry = new ZipEntry("Invoice_Room_" + inv.getRoomNo() + ".pdf");
                zos.putNextEntry(entry);
                zos.write(pdf);
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate bulk ZIP", e);
        }
    }
}