package com.bizcore.company.infrastructure.web;

import com.bizcore.company.application.dto.EmployeeResponse;
import com.bizcore.company.application.dto.InviteEmployeeRequest;
import com.bizcore.company.application.dto.UpdateEmployeeRequest;
import com.bizcore.company.domain.port.in.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Gestión de empleados")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final ListEmployeesUseCase listEmployeesUseCase;
    private final InviteEmployeeUseCase inviteEmployeeUseCase;
    private final UpdateEmployeeUseCase updateEmployeeUseCase;
    private final DeactivateEmployeeUseCase deactivateEmployeeUseCase;

    @GetMapping
    @Operation(summary = "Listar empleados (paginado)")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(
                listEmployeesUseCase.list(TenantContext.getTenantId(), pageable)));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo empleado")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> invite(
            @Valid @RequestBody InviteEmployeeRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(inviteEmployeeUseCase.invite(TenantContext.getTenantId(), request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar empleado")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmployeeRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                updateEmployeeUseCase.update(TenantContext.getTenantId(), id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar empleado")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        deactivateEmployeeUseCase.deactivate(TenantContext.getTenantId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Empleado desactivado"));
    }
}
