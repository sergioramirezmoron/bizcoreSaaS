package com.bizcore.sales.infrastructure.web;

import com.bizcore.sales.application.dto.CustomerRequest;
import com.bizcore.sales.application.dto.CustomerResponse;
import com.bizcore.sales.domain.port.in.*;
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
@RequestMapping("/api/v1/sales/customers")
@RequiredArgsConstructor
@Tag(name = "Sales - Customers", description = "CRM de clientes")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final ListCustomersUseCase listCustomersUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;

    @GetMapping
    @Operation(summary = "Listar clientes con búsqueda")
    public ResponseEntity<ApiResponse<PageResponse<CustomerResponse>>> list(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(listCustomersUseCase.list(TenantContext.getTenantId(), search, pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente")
    public ResponseEntity<ApiResponse<CustomerResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(getCustomerUseCase.get(TenantContext.getTenantId(), id)));
    }

    @PostMapping
    @Operation(summary = "Crear cliente")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<CustomerResponse>> create(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(createCustomerUseCase.create(TenantContext.getTenantId(), request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                updateCustomerUseCase.update(TenantContext.getTenantId(), id, request)));
    }
}
