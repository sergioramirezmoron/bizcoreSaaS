package com.bizcore.inventory.application.dto;

import com.bizcore.inventory.domain.model.StockItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StockItemResponse(
        UUID id,
        UUID productId,
        UUID variantId,
        UUID branchId,
        BigDecimal quantity,
        BigDecimal minQuantity,
        BigDecimal maxQuantity,
        String location,
        boolean lowStock,
        Instant lastUpdatedAt
) {
    public static StockItemResponse from(StockItem s) {
        boolean low = s.minQuantity() != null
                && s.quantity().compareTo(s.minQuantity()) <= 0;
        return new StockItemResponse(
                s.id(), s.productId(), s.variantId(), s.branchId(),
                s.quantity(), s.minQuantity(), s.maxQuantity(),
                s.location(), low, s.lastUpdatedAt()
        );
    }
}
