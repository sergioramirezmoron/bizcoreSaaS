package com.bizcore.reports.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record StockReportRow(
        UUID productId,
        String productName,
        String sku,
        BigDecimal quantity,
        BigDecimal minQuantity,
        boolean belowMinimum
) {}
