package com.bizcore.company.infrastructure.web;

import com.bizcore.company.application.dto.CompanyResponse;
import com.bizcore.company.application.dto.UpdateCompanyRequest;
import com.bizcore.company.domain.port.in.GetCompanyUseCase;
import com.bizcore.company.domain.port.in.UpdateCompanyUseCase;
import com.bizcore.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/company")
@RequiredArgsConstructor
@Tag(name = "Company", description = "Gestión de la empresa")
@SecurityRequirement(name = "bearerAuth")
public class CompanyController {

    private final GetCompanyUseCase getCompanyUseCase;
    private final UpdateCompanyUseCase updateCompanyUseCase;

    @GetMapping
    @Operation(summary = "Obtener información de la empresa")
    public ResponseEntity<ApiResponse<CompanyResponse>> get(
            @AuthenticationPrincipal UUID userId
    ) {
        // El tenantId viene en el SecurityContext a través del JWT — aquí lo extraemos del thread local
        UUID tenantId = com.bizcore.shared.tenant.TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.ok(getCompanyUseCase.getCompany(tenantId)));
    }

    @PutMapping
    @Operation(summary = "Actualizar configuración de la empresa")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CompanyResponse>> update(
            @Valid @RequestBody UpdateCompanyRequest request
    ) {
        UUID tenantId = com.bizcore.shared.tenant.TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.ok(updateCompanyUseCase.update(tenantId, request)));
    }
}
