package com.bizcore.sales.application.dto;

import com.bizcore.sales.domain.model.Order;
import com.bizcore.sales.domain.model.OrderStatus;
import com.bizcore.sales.domain.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderSummaryResponse(
        UUID id,
        String orderNumber,
        UUID branchId,
        UUID customerId,
        OrderStatus status,
        BigDecimal total,
        PaymentMethod paymentMethod,
        Instant createdAt
) {
    public static OrderSummaryResponse from(Order o) {
        return new OrderSummaryResponse(o.id(), o.orderNumber(), o.branchId(),
                o.customerId(), o.status(), o.total(), o.paymentMethod(), o.createdAt());
    }
}
