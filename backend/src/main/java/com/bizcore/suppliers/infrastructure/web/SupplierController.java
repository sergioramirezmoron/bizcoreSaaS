package com.bizcore.suppliers.infrastructure.web;

import com.bizcore.company.application.PlanGuard;
import com.bizcore.shared.response.ApiResponse;
import com.bizcore.shared.tenant.TenantContext;
import com.bizcore.suppliers.application.dto.SupplierRequest;
import com.bizcore.suppliers.application.dto.SupplierResponse;
import com.bizcore.suppliers.domain.port.in.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "Gestión de proveedores (plan Standard+)")
@SecurityRequirement(name = "bearerAuth")
public class SupplierController {

    private final CreateSupplierUseCase createUseCase;
    private final UpdateSupplierUseCase updateUseCase;
    private final DeleteSupplierUseCase deleteUseCase;
    private final ListSuppliersUseCase listUseCase;
    private final GetSupplierUseCase getUseCase;
    private final PlanGuard planGuard;

    @ModelAttribute
    public void checkPlan() {
        planGuard.requireStandard(TenantContext.getTenantId());
    }

    @GetMapping
    @Operation(summary = "Listar proveedores activos")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> list() {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.ok(listUseCase.list(tenantId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proveedor por ID")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<SupplierResponse>> get(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.ok(getUseCase.get(tenantId, id)));
    }

    @PostMapping
    @Operation(summary = "Crear proveedor")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<SupplierResponse>> create(@Valid @RequestBody SupplierRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Proveedor creado", createUseCase.create(tenantId, request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proveedor")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<SupplierResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody SupplierRequest request
    ) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.ok("Proveedor actualizado", updateUseCase.update(tenantId, id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proveedor (borrado lógico)")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        deleteUseCase.delete(tenantId, id);
        return ResponseEntity.ok(ApiResponse.ok("Proveedor eliminado"));
    }
}
