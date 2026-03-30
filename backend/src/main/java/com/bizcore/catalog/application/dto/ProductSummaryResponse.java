package com.bizcore.catalog.application.dto;

import com.bizcore.catalog.domain.model.Product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryResponse(
        UUID id,
        UUID categoryId,
        String name,
        String sku,
        BigDecimal sellingPrice,
        boolean active
) {
    public static ProductSummaryResponse from(Product p) {
        return new ProductSummaryResponse(p.id(), p.categoryId(), p.name(), p.sku(), p.sellingPrice(), p.active());
    }
}
