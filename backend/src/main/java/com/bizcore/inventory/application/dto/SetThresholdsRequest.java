package com.bizcore.inventory.application.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record SetThresholdsRequest(
        @DecimalMin("0.000") BigDecimal minQuantity,
        @DecimalMin("0.000") BigDecimal maxQuantity,  // null = sin límite
        String location
) {}
