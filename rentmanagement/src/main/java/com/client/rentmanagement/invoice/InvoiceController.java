package com.client.rentmanagement.invoice;

import com.client.rentmanagement.invoice.entity.Invoice;
import com.client.rentmanagement.invoice.repository.InvoiceRepository;
import com.client.rentmanagement.invoice.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;

    @Autowired
    private PdfService pdfService;

    public InvoiceController(InvoiceService invoiceService, InvoiceRepository invoiceRepository) {
        this.invoiceService = invoiceService;
        this.invoiceRepository = invoiceRepository;
    }

    @PostMapping("/bulk")
    public ResponseEntity<String> generateBulk(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam int totalCommonUnits) {
        return ResponseEntity.ok(invoiceService.generateBulkInvoices(month, totalCommonUnits));
    }

    @PostMapping("/tenant/{tenantId}")
    public ResponseEntity<Invoice> generateForTenant(
            @PathVariable UUID tenantId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam(defaultValue = "0") BigDecimal commonUnitsShare) {
        
        Invoice invoice = invoiceService.generateInvoice(tenantId, month, commonUnitsShare);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/month/{month}")
    public ResponseEntity<List<Invoice>> getInvoicesByMonth(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        LocalDate date = month.atDay(1);
        return ResponseEntity.ok(invoiceRepository.findByBillingMonth(date));
    }

    @GetMapping("/tenant/{tenantId}/history")
    public ResponseEntity<List<Invoice>> getTenantHistory(@PathVariable UUID tenantId) {
        return ResponseEntity.ok(invoiceRepository.findByTenantId(tenantId));
    }

    /**
     * ADDED: Download a specific PDF by its unique ID
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable UUID id) {
        Invoice invoice = invoiceService.getInvoiceById(id); 
        byte[] pdfBytes = pdfService.generateInvoicePdf(invoice);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + invoice.getRoomNo() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    /**
     * ADDED: Download a ZIP of all invoices for the month
*/
    @GetMapping("/month/{month}/download-all")
    public ResponseEntity<byte[]> downloadAllInvoices(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        byte[] zipContents = invoiceService.generateInvoicesZip(month);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Invoices_" + month + ".zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipContents);
    }
}  