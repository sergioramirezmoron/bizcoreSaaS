package com.bizcore.superadmin.infrastructure.web;

import com.bizcore.company.domain.model.SubscriptionPlan;
import com.bizcore.company.domain.model.SubscriptionStatus;
import com.bizcore.shared.response.ApiResponse;
import com.bizcore.shared.response.PageResponse;
import com.bizcore.superadmin.application.dto.*;
import com.bizcore.superadmin.domain.model.PlatformDashboard;
import com.bizcore.superadmin.domain.port.in.*;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/api/v1")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "SuperAdmin", description = "Panel de administración de la plataforma BizCore")
@SecurityRequirement(name = "bearerAuth")
public class SuperAdminController {

    private final GetPlatformDashboardUseCase dashboardUseCase;
    private final ListCompaniesUseCase        listCompaniesUseCase;
    private final GetCompanyDetailUseCase     getCompanyDetailUseCase;
    private final UpdateCompanyNotesUseCase   updateNotesUseCase;
    private final PerformAdminActionUseCase   performActionUseCase;
    private final ImpersonateUserUseCase      impersonateUseCase;
    private final ListPromoCodesUseCase       listPromoCodesUseCase;
    private final CreatePromoCodeUseCase      createPromoCodeUseCase;
    private final DeactivatePromoCodeUseCase  deactivatePromoCodeUseCase;

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    @Operation(summary = "Estadísticas globales de la plataforma")
    public ResponseEntity<ApiResponse<PlatformDashboard>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardUseCase.getDashboard()));
    }

    // ── Companies ─────────────────────────────────────────────────────────────

    @GetMapping("/companies")
    @Operation(summary = "Listar empresas con filtros y paginación")
    public ResponseEntity<ApiResponse<PageResponse<CompanySummaryDto>>> listCompanies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) SubscriptionPlan plan,
            @RequestParam(required = false) SubscriptionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(listCompaniesUseCase.list(search, plan, status, pageable)));
    }

    @GetMapping("/companies/{id}")
    @Operation(summary = "Detalle completo de una empresa con últimas acciones admin")
    public ResponseEntity<ApiResponse<CompanyDetailDto>> getCompany(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(getCompanyDetailUseCase.get(id)));
    }

    @PatchMapping("/companies/{id}/notes")
    @Operation(summary = "Actualizar notas internas del admin sobre una empresa")
    public ResponseEntity<ApiResponse<Void>> updateNotes(
            @PathVariable UUID id,
            @RequestBody UpdateCompanyNotesRequest request
    ) {
        updateNotesUseCase.updateNotes(id, request.adminNotes());
        return ResponseEntity.ok(ApiResponse.ok("Notas actualizadas"));
    }

    // ── Admin Actions ─────────────────────────────────────────────────────────

    @PostMapping("/companies/{id}/actions")
    @Operation(summary = "Ejecutar acción administrativa sobre una empresa")
    public ResponseEntity<ApiResponse<AdminActionDto>> performAction(
            @PathVariable UUID id,
            @Valid @RequestBody AdminActionRequest request,
            @AuthenticationPrincipal UUID performedBy
    ) {
        AdminActionDto result = performActionUseCase.perform(id, performedBy, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Acción ejecutada", result));
    }

    // ── Impersonation ─────────────────────────────────────────────────────────

    @PostMapping("/companies/{id}/impersonate")
    @Operation(summary = "Generar token de acceso temporal para un usuario de una empresa")
    public ResponseEntity<ApiResponse<ImpersonationTokenResponse>> impersonate(
            @PathVariable UUID id,
            @Valid @RequestBody ImpersonateRequest request
    ) {
        ImpersonationTokenResponse token = impersonateUseCase.impersonate(id, request.targetUserId());
        return ResponseEntity.ok(ApiResponse.ok(token));
    }

    // ── Promo Codes ───────────────────────────────────────────────────────────

    @GetMapping("/promo-codes")
    @Operation(summary = "Listar todos los códigos promocionales")
    public ResponseEntity<ApiResponse<PageResponse<PromoCodeAdminDto>>> listPromoCodes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(listPromoCodesUseCase.list(pageable)));
    }

    @PostMapping("/promo-codes")
    @Operation(summary = "Crear nuevo código promocional")
    public ResponseEntity<ApiResponse<PromoCodeAdminDto>> createPromoCode(
            @Valid @RequestBody CreatePromoCodeRequest request,
            @AuthenticationPrincipal UUID createdBy
    ) {
        PromoCodeAdminDto created = createPromoCodeUseCase.create(createdBy, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Código creado", created));
    }

    @DeleteMapping("/promo-codes/{id}")
    @Operation(summary = "Desactivar código promocional")
    public ResponseEntity<ApiResponse<Void>> deactivatePromoCode(@PathVariable UUID id) {
        deactivatePromoCodeUseCase.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("Código desactivado"));
    }
}
