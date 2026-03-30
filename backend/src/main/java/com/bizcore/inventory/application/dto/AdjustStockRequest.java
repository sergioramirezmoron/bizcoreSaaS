package com.bizcore.inventory.application.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AdjustStockRequest(
        @NotNull BigDecimal quantityChange,   // positivo = entrada, negativo = salida
        String notes
) {}
