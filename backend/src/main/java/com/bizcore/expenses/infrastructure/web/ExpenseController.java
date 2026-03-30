package com.bizcore.expenses.infrastructure.web;

import com.bizcore.expenses.application.dto.ExpenseRequest;
import com.bizcore.expenses.application.dto.ExpenseResponse;
import com.bizcore.expenses.application.dto.ExpenseSummaryResponse;
import com.bizcore.expenses.domain.model.ExpenseCategory;
import com.bizcore.expenses.domain.model.ExpenseStatus;
import com.bizcore.expenses.domain.port.in.*;
import com.bizcore.shared.response.ApiResponse;
import com.bizcore.shared.response.PageResponse;
import com.bizcore.shared.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Gestión de gastos del negocio")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {

    private final ListExpensesUseCase listExpensesUseCase;
    private final GetExpenseUseCase getExpenseUseCase;
    private final CreateExpenseUseCase createExpenseUseCase;
    private final UpdateExpenseUseCase updateExpenseUseCase;
    private final MarkExpensePaidUseCase markExpensePaidUseCase;
    private final CancelExpenseUseCase cancelExpenseUseCase;

    @GetMapping
    @Operation(summary = "Listar gastos con filtros (sucursal, categoría, estado, rango de fechas)")
    public ResponseEntity<ApiResponse<PageResponse<ExpenseSummaryResponse>>> list(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) ExpenseCategory category,
            @RequestParam(required = false) ExpenseStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(
                listExpensesUseCase.list(TenantContext.getTenantId(), branchId, category, status, from, to, pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un gasto")
    public ResponseEntity<ApiResponse<ExpenseResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(getExpenseUseCase.get(TenantContext.getTenantId(), id)));
    }

    @PostMapping
    @Operation(summary = "Crear gasto (queda en PENDING)")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> create(
            @Valid @RequestBody ExpenseRequest request,
            @AuthenticationPrincipal UUID userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(createExpenseUseCase.create(TenantContext.getTenantId(), userId, request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar gasto (solo si está en PENDING)")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ExpenseRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                updateExpenseUseCase.update(TenantContext.getTenantId(), id, request)));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "Marcar gasto como pagado")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> pay(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(
                markExpensePaidUseCase.markPaid(TenantContext.getTenantId(), id)));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancelar gasto")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable UUID id) {
        cancelExpenseUseCase.cancel(TenantContext.getTenantId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Gasto cancelado"));
    }
}
