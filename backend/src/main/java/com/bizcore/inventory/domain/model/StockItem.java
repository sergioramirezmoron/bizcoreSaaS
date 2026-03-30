package com.bizcore.inventory.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StockItem(
        UUID id,
        UUID tenantId,
        UUID productId,    // null si es variante
        UUID variantId,    // null si es producto base
        UUID branchId,
        BigDecimal quantity,
        BigDecimal minQuantity,
        BigDecimal maxQuantity,    // null = sin límite
        String location,
        Instant lastUpdatedAt
) {}
