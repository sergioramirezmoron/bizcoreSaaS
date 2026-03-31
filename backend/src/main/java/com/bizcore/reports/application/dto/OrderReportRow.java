package com.bizcore.reports.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderReportRow(
        UUID orderId,
        String orderNumber,
        String status,
        BigDecimal total,
        String paymentMethod,
        Instant createdAt
) {}
