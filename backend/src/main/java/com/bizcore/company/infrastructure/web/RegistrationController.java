package com.bizcore.company.infrastructure.web;

import com.bizcore.company.application.dto.BusinessTypeResponse;
import com.bizcore.company.application.dto.RegisterCompanyRequest;
import com.bizcore.company.application.dto.RegisterCompanyResponse;
import com.bizcore.company.domain.port.in.ListBusinessTypesUseCase;
import com.bizcore.company.domain.port.in.RegisterCompanyUseCase;
import com.bizcore.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/registration")
@RequiredArgsConstructor
@Tag(name = "Registration", description = "Registro de nuevas empresas")
public class RegistrationController {

    private final RegisterCompanyUseCase registerCompanyUseCase;
    private final ListBusinessTypesUseCase listBusinessTypesUseCase;

    @PostMapping
    @Operation(summary = "Registrar nueva empresa y crear usuario OWNER")
    public ResponseEntity<ApiResponse<RegisterCompanyResponse>> register(
            @Valid @RequestBody RegisterCompanyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Empresa registrada correctamente", registerCompanyUseCase.register(request)));
    }

    @GetMapping("/business-types")
    @Operation(summary = "Listar tipos de negocio disponibles")
    public ResponseEntity<ApiResponse<List<BusinessTypeResponse>>> businessTypes() {
        return ResponseEntity.ok(ApiResponse.ok(listBusinessTypesUseCase.listAll()));
    }
}
