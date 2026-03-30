package com.bizcore.company.infrastructure.web;

import com.bizcore.company.application.dto.BranchRequest;
import com.bizcore.company.application.dto.BranchResponse;
import com.bizcore.company.domain.port.in.*;
import com.bizcore.shared.response.ApiResponse;
import com.bizcore.shared.tenant.TenantContext;
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
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@Tag(name = "Branches", description = "Gestión de sucursales")
@SecurityRequirement(name = "bearerAuth")
public class BranchController {

    private final ListBranchesUseCase listBranchesUseCase;
    private final CreateBranchUseCase createBranchUseCase;
    private final UpdateBranchUseCase updateBranchUseCase;
    private final DeleteBranchUseCase deleteBranchUseCase;

    @GetMapping
    @Operation(summary = "Listar sucursales de la empresa")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(listBranchesUseCase.list(TenantContext.getTenantId())));
    }

    @PostMapping
    @Operation(summary = "Crear nueva sucursal")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> create(@Valid @RequestBody BranchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(createBranchUseCase.create(TenantContext.getTenantId(), request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar sucursal")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody BranchRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                updateBranchUseCase.update(TenantContext.getTenantId(), id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sucursal (no aplica a la principal)")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        deleteBranchUseCase.delete(TenantContext.getTenantId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Sucursal eliminada"));
    }
}
