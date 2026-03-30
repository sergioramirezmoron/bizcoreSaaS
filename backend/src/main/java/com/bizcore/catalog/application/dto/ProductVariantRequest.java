package com.bizcore.catalog.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Map;

public record ProductVariantRequest(
        @NotBlank @Size(max = 100) String variantName,
        @Size(max = 100) String sku,
        Map<String, Object> attributes,
        @DecimalMin("0.00") BigDecimal sellingPrice,
        boolean active
) {}
