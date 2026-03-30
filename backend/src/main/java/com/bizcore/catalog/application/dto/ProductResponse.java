package com.bizcore.catalog.application.dto;

import com.bizcore.catalog.domain.model.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID categoryId,
        String name,
        String description,
        String sku,
        BigDecimal purchasePrice,
        BigDecimal sellingPrice,
        BigDecimal taxRate,
        String unit,
        Map<String, Object> customFields,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        List<ProductImageResponse> images,
        List<ProductVariantResponse> variants
) {
    public static ProductResponse from(Product p, List<ProductImageResponse> images, List<ProductVariantResponse> variants) {
        return new ProductResponse(
                p.id(), p.categoryId(), p.name(), p.description(), p.sku(),
                p.purchasePrice(), p.sellingPrice(), p.taxRate(), p.unit(),
                p.customFields(), p.active(), p.createdAt(), p.updatedAt(),
                images, variants
        );
    }
}
