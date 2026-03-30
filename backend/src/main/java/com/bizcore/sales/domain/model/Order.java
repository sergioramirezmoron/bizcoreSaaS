package com.bizcore.sales.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Order(
        UUID id,
        UUID tenantId,
        UUID branchId,
        String orderNumber,
        UUID employeeId,
        UUID customerId,
        OrderStatus status,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal total,
        PaymentMethod paymentMethod,
        String notes,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt
) {}
