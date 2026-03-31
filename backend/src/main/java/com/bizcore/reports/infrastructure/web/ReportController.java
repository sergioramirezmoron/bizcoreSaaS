package com.bizcore.reports.infrastructure.web;

import com.bizcore.company.application.PlanGuard;
import com.bizcore.reports.application.dto.*;
import com.bizcore.reports.domain.model.ReportPeriod;
import com.bizcore.reports.infrastructure.export.ExcelExportService;
import com.bizcore.reports.infrastructure.export.PdfExportService;
import com.bizcore.reports.infrastructure.persistence.ReportQueryRepository;
import com.bizcore.shared.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Generación de reportes y exportaciones (plan Standard+)")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportQueryRepository queryRepo;
    private final ExcelExportService excelService;
    private final PdfExportService pdfService;
    private final PlanGuard planGuard;

    @ModelAttribute
    public void checkPlan() {
        planGuard.requireStandard(TenantContext.getTenantId());
    }

    // ── Orders ──────────────────────────────────────────────────────────────

    @GetMapping("/orders")
    @Operation(summary = "Reporte de pedidos por período (JSON)")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MANAGER','VIEWER')")
    public ResponseEntity<List<OrderReportRow>> ordersJson(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        ReportPeriod period = resolvePeriod(from, to);
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(queryRepo.findOrdersByPeriod(tenantId, period.from(), period.to()));
    }

    @GetMapping("/orders/export/excel")
    @Operation(summary = "Exportar pedidos a Excel")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<byte[]> ordersExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        ReportPeriod period = resolvePeriod(from, to);
        UUID tenantId = TenantContext.getTenantId();
        List<OrderReportRow> rows = queryRepo.findOrdersByPeriod(tenantId, period.from(), period.to());
        byte[] data = excelService.exportOrders(rows, period.from() + " / " + period.to());
        return fileResponse(data, "pedidos.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping("/orders/export/pdf")
    @Operation(summary = "Exportar pedidos a PDF")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<byte[]> ordersPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        ReportPeriod period = resolvePeriod(from, to);
        UUID tenantId = TenantContext.getTenantId();
        List<OrderReportRow> rows = queryRepo.findOrdersByPeriod(tenantId, period.from(), period.to());
        byte[] data = pdfService.exportOrders(rows, period.from() + " / " + period.to());
        return fileResponse(data, "pedidos.pdf", MediaType.APPLICATION_PDF_VALUE);
    }

    // ── Stock ──────────────────────────────────────────────────────────────

    @GetMapping("/stock")
    @Operation(summary = "Reporte de stock actual (JSON)")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MANAGER','VIEWER')")
    public ResponseEntity<List<StockReportRow>> stockJson() {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(queryRepo.findStockReport(tenantId));
    }

    @GetMapping("/stock/export/excel")
    @Operation(summary = "Exportar stock a Excel")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<byte[]> stockExcel() {
        UUID tenantId = TenantContext.getTenantId();
        byte[] data = excelService.exportStock(queryRepo.findStockReport(tenantId));
        return fileResponse(data, "stock.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping("/stock/export/pdf")
    @Operation(summary = "Exportar stock a PDF")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<byte[]> stockPdf() {
        UUID tenantId = TenantContext.getTenantId();
        byte[] data = pdfService.exportStock(queryRepo.findStockReport(tenantId));
        return fileResponse(data, "stock.pdf", MediaType.APPLICATION_PDF_VALUE);
    }

    // ── Expenses ───────────────────────────────────────────────────────────

    @GetMapping("/expenses")
    @Operation(summary = "Reporte de gastos por período (JSON)")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','VIEWER')")
    public ResponseEntity<List<ExpenseReportRow>> expensesJson(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        ReportPeriod period = resolvePeriod(from, to);
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(queryRepo.findExpensesByPeriod(tenantId, period.from(), period.to()));
    }

    @GetMapping("/expenses/export/excel")
    @Operation(summary = "Exportar gastos a Excel")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<byte[]> expensesExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        ReportPeriod period = resolvePeriod(from, to);
        UUID tenantId = TenantContext.getTenantId();
        List<ExpenseReportRow> rows = queryRepo.findExpensesByPeriod(tenantId, period.from(), period.to());
        byte[] data = excelService.exportExpenses(rows, period.from() + " / " + period.to());
        return fileResponse(data, "gastos.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping("/expenses/export/pdf")
    @Operation(summary = "Exportar gastos a PDF")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<byte[]> expensesPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        ReportPeriod period = resolvePeriod(from, to);
        UUID tenantId = TenantContext.getTenantId();
        List<ExpenseReportRow> rows = queryRepo.findExpensesByPeriod(tenantId, period.from(), period.to());
        byte[] data = pdfService.exportExpenses(rows, period.from() + " / " + period.to());
        return fileResponse(data, "gastos.pdf", MediaType.APPLICATION_PDF_VALUE);
    }

    // ── Top Products ───────────────────────────────────────────────────────

    @GetMapping("/top-products")
    @Operation(summary = "Top productos más vendidos (JSON)")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MANAGER','VIEWER')")
    public ResponseEntity<List<TopProductRow>> topProductsJson(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "10") int limit
    ) {
        ReportPeriod period = resolvePeriod(from, to);
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(queryRepo.findTopProducts(tenantId, period.from(), period.to(), limit));
    }

    @GetMapping("/top-products/export/excel")
    @Operation(summary = "Exportar top productos a Excel")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<byte[]> topProductsExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "20") int limit
    ) {
        ReportPeriod period = resolvePeriod(from, to);
        UUID tenantId = TenantContext.getTenantId();
        List<TopProductRow> rows = queryRepo.findTopProducts(tenantId, period.from(), period.to(), limit);
        byte[] data = excelService.exportTopProducts(rows, period.from() + " / " + period.to());
        return fileResponse(data, "top-productos.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @GetMapping("/top-products/export/pdf")
    @Operation(summary = "Exportar top productos a PDF")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<byte[]> topProductsPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "20") int limit
    ) {
        ReportPeriod period = resolvePeriod(from, to);
        UUID tenantId = TenantContext.getTenantId();
        List<TopProductRow> rows = queryRepo.findTopProducts(tenantId, period.from(), period.to(), limit);
        byte[] data = pdfService.exportTopProducts(rows, period.from() + " / " + period.to());
        return fileResponse(data, "top-productos.pdf", MediaType.APPLICATION_PDF_VALUE);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private ReportPeriod resolvePeriod(LocalDate from, LocalDate to) {
        if (from == null && to == null) return ReportPeriod.currentMonth();
        LocalDate f = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate t = to != null ? to : LocalDate.now();
        return new ReportPeriod(f, t);
    }

    private ResponseEntity<byte[]> fileResponse(byte[] data, String filename, String contentType) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(data.length)
                .body(data);
    }
}
