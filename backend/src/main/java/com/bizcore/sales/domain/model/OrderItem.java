package com.bizcore.sales.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItem(
        UUID id,
        UUID orderId,
        UUID tenantId,
        UUID productId,      // null si no hay producto en catálogo
        UUID variantId,
        String productName,  // snapshot del nombre al momento de la venta
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal taxRate,
        BigDecimal discountPercent,
        BigDecimal lineTotal
) {}
