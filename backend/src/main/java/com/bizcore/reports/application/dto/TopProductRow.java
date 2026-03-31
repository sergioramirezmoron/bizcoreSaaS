package com.bizcore.reports.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TopProductRow(
        UUID productId,
        String productName,
        BigDecimal totalQuantitySold,
        BigDecimal totalRevenue,
        long orderCount
) {}
