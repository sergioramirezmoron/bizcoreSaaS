package com.bizcore.inventory.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record InitStockItemRequest(
        UUID productId,        // obligatorio si variantId es null
        UUID variantId,        // obligatorio si productId es null
        UUID branchId,
        @NotNull @DecimalMin("0.000") BigDecimal initialQuantity,
        @DecimalMin("0.000") BigDecimal minQuantity,
        @DecimalMin("0.000") BigDecimal maxQuantity,
        String location
) {}
