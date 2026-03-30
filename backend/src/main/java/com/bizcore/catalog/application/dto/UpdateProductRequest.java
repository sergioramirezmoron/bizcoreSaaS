package com.bizcore.catalog.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record UpdateProductRequest(
        UUID categoryId,
        @NotBlank @Size(max = 200) String name,
        String description,
        @Size(max = 100) String sku,
        @NotNull @DecimalMin("0.00") BigDecimal purchasePrice,
        @NotNull @DecimalMin("0.00") BigDecimal sellingPrice,
        @DecimalMin("0.00") BigDecimal taxRate,
        @Size(max = 50) String unit,
        Map<String, Object> customFields,
        boolean active
) {}
