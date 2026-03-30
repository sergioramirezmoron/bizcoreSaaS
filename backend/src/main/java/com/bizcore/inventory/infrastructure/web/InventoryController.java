package com.bizcore.inventory.infrastructure.web;

import com.bizcore.inventory.application.dto.*;
import com.bizcore.inventory.domain.port.in.*;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory/stock")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Gestión de stock y movimientos")
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

    private final ListStockItemsUseCase listStockItemsUseCase;
    private final GetStockItemUseCase getStockItemUseCase;
    private final InitStockItemUseCase initStockItemUseCase;
    private final AdjustStockUseCase adjustStockUseCase;
    private final SetStockThresholdsUseCase setStockThresholdsUseCase;
    private final ListStockMovementsUseCase listStockMovementsUseCase;
    private final ListLowStockUseCase listLowStockUseCase;

    @GetMapping
    @Operation(summary = "Listar items de stock (filtros opcionales por producto, variante, sucursal)")
    public ResponseEntity<ApiResponse<PageResponse<StockItemResponse>>> list(
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) UUID variantId,
            @RequestParam(required = false) UUID branchId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("lastUpdatedAt").descending());
        var result = listStockItemsUseCase.list(TenantContext.getTenantId(), productId, variantId, branchId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    @GetMapping("/low")
    @Operation(summary = "Items con stock igual o por debajo del mínimo")
    public ResponseEntity<ApiResponse<List<StockItemResponse>>> lowStock(
            @RequestParam(required = false) UUID branchId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                listLowStockUseCase.listLowStock(TenantContext.getTenantId(), branchId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener item de stock por id")
    public ResponseEntity<ApiResponse<StockItemResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(
                getStockItemUseCase.get(TenantContext.getTenantId(), id)));
    }

    @PostMapping
    @Operation(summary = "Inicializar stock para un producto/variante en una sucursal")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<StockItemResponse>> init(
            @Valid @RequestBody InitStockItemRequest request,
            @AuthenticationPrincipal UUID userId
    ) {
        StockItemResponse response = initStockItemUseCase.init(TenantContext.getTenantId(), userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PostMapping("/{id}/adjust")
    @Operation(summary = "Ajuste manual de stock (positivo = entrada, negativo = salida)")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<StockMovementResponse>> adjust(
            @PathVariable UUID id,
            @Valid @RequestBody AdjustStockRequest request,
            @AuthenticationPrincipal UUID userId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                adjustStockUseCase.adjust(TenantContext.getTenantId(), id, userId, request)));
    }

    @PutMapping("/{id}/thresholds")
    @Operation(summary = "Actualizar umbrales mínimo/máximo y ubicación")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<StockItemResponse>> setThresholds(
            @PathVariable UUID id,
            @Valid @RequestBody SetThresholdsRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                setStockThresholdsUseCase.setThresholds(TenantContext.getTenantId(), id, request)));
    }

    @GetMapping("/{id}/movements")
    @Operation(summary = "Historial de movimientos de un item de stock")
    public ResponseEntity<ApiResponse<PageResponse<StockMovementResponse>>> movements(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var result = listStockMovementsUseCase.list(TenantContext.getTenantId(), id, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }
}
