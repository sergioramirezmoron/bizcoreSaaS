package com.bizcore.catalog.domain.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record ProductVariant(
        UUID id,
        UUID tenantId,
        UUID productId,
        String variantName,
        String sku,
        Map<String, Object> attributes,   // { "color": "rojo", "talla": "M" }
        BigDecimal sellingPrice,          // null = usa precio del producto
        boolean active
) {}
