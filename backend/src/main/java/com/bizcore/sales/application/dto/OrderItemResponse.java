package com.bizcore.sales.application.dto;

import com.bizcore.sales.domain.model.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID productId,
        UUID variantId,
        String productName,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal taxRate,
        BigDecimal discountPercent,
        BigDecimal lineTotal
) {
    public static OrderItemResponse from(OrderItem i) {
        return new OrderItemResponse(i.id(), i.productId(), i.variantId(), i.productName(),
                i.quantity(), i.unitPrice(), i.taxRate(), i.discountPercent(), i.lineTotal());
    }
}
