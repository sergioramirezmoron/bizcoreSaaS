package com.bizcore.sales.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemRequest(
        UUID productId,
        UUID variantId,
        @NotBlank @Size(max = 200) String productName,
        @NotNull @DecimalMin("0.001") BigDecimal quantity,
        @NotNull @DecimalMin("0.00") BigDecimal unitPrice,
        @DecimalMin("0.00") BigDecimal taxRate,
        @DecimalMin("0.00") BigDecimal discountPercent
) {}
