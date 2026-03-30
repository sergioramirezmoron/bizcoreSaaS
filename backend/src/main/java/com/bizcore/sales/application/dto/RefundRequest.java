package com.bizcore.sales.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RefundRequest(
        @NotNull @DecimalMin("0.01") java.math.BigDecimal refundAmount,
        String reason,
        @Pattern(regexp = "FULL|PARTIAL") String refundType,
        String refundMethod,
        boolean restoreStock
) {}
