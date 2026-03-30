package com.bizcore.catalog.application.dto;

import com.bizcore.catalog.domain.model.ProductVariant;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record ProductVariantResponse(
        UUID id,
        String variantName,
        String sku,
        Map<String, Object> attributes,
        BigDecimal sellingPrice,
        boolean active
) {
    public static ProductVariantResponse from(ProductVariant v) {
        return new ProductVariantResponse(v.id(), v.variantName(), v.sku(), v.attributes(), v.sellingPrice(), v.active());
    }
}
