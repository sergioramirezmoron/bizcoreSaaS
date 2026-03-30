package com.bizcore.sales.infrastructure.web;

import com.bizcore.sales.application.dto.*;
import com.bizcore.sales.domain.model.OrderStatus;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales/orders")
@RequiredArgsConstructor
@Tag(name = "Sales - Orders", description = "Pedidos, cobros y devoluciones")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final ListOrdersUseCase listOrdersUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final CreateOrderUseCase createOrderUseCase;
    private final CompleteOrderUseCase completeOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final CreateRefundUseCase createRefundUseCase;

    @GetMapping
    @Operation(summary = "Listar pedidos con filtros (sucursal, cliente, estado)")
    public ResponseEntity<ApiResponse<PageResponse<OrderSummaryResponse>>> list(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(
                listOrdersUseCase.list(TenantContext.getTenantId(), branchId, customerId, status, pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido con líneas")
    public ResponseEntity<ApiResponse<OrderResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(getOrderUseCase.get(TenantContext.getTenantId(), id)));
    }

    @PostMapping
    @Operation(summary = "Crear pedido (queda en PENDING hasta cobro)")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal UUID userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(createOrderUseCase.create(TenantContext.getTenantId(), userId, request)));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Completar pedido (registra cobro y descuenta stock)")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<OrderResponse>> complete(
            @PathVariable UUID id,
            @Valid @RequestBody CompleteOrderRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                completeOrderUseCase.complete(TenantContext.getTenantId(), id, request)));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancelar pedido PENDING")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable UUID id) {
        cancelOrderUseCase.cancel(TenantContext.getTenantId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Pedido cancelado"));
    }

    @PostMapping("/{id}/refunds")
    @Operation(summary = "Crear devolución (total o parcial)")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<RefundResponse>> refund(
            @PathVariable UUID id,
            @Valid @RequestBody RefundRequest request,
            @AuthenticationPrincipal UUID userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(createRefundUseCase.refund(TenantContext.getTenantId(), id, userId, request)));
    }
}
