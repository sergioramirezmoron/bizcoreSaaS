package com.bizcore.sales.application.dto;

import com.bizcore.sales.domain.model.Order;
import com.bizcore.sales.domain.model.OrderStatus;
import com.bizcore.sales.domain.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
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
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order o, List<OrderItemResponse> items) {
        return new OrderResponse(o.id(), o.branchId(), o.orderNumber(), o.employeeId(),
                o.customerId(), o.status(), o.subtotal(), o.discountAmount(), o.taxAmount(),
                o.total(), o.paymentMethod(), o.notes(), o.completedAt(), o.createdAt(), items);
    }
}
