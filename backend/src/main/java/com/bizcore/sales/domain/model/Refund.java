package com.bizcore.sales.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Refund(
        UUID id,
        UUID tenantId,
        UUID orderId,
        UUID employeeId,
        BigDecimal refundAmount,
        String reason,
        String refundType,    // FULL | PARTIAL
        String refundMethod,
        Instant createdAt
) {}
