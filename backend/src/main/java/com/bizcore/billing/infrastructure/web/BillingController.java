package com.bizcore.billing.infrastructure.web;

import com.bizcore.billing.application.dto.CheckoutRequest;
import com.bizcore.billing.application.dto.CheckoutResponse;
import com.bizcore.billing.application.dto.PromoCodeResponse;
import com.bizcore.billing.domain.port.in.CreateCheckoutSessionUseCase;
import com.bizcore.billing.domain.port.in.ValidatePromoCodeUseCase;
import com.bizcore.shared.response.ApiResponse;
import com.bizcore.shared.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
@Tag(name = "Billing", description = "Suscripciones Stripe y códigos promocionales")
@SecurityRequirement(name = "bearerAuth")
public class BillingController {

    private final CreateCheckoutSessionUseCase createCheckoutSessionUseCase;
    private final ValidatePromoCodeUseCase validatePromoCodeUseCase;

    @PostMapping("/checkout")
    @Operation(summary = "Crear sesión de pago en Stripe para contratar un plan")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(
            @Valid @RequestBody CheckoutRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                createCheckoutSessionUseCase.createSession(TenantContext.getTenantId(), request)));
    }

    @GetMapping("/promo-codes/{code}")
    @Operation(summary = "Validar un código promocional")
    public ResponseEntity<ApiResponse<PromoCodeResponse>> validatePromoCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(ApiResponse.ok(validatePromoCodeUseCase.validate(code)));
    }
}
