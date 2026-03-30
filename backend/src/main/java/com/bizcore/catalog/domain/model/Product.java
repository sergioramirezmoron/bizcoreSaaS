package com.bizcore.catalog.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record Product(
        UUID id,
        UUID tenantId,
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
        Instant updatedAt
) {}
