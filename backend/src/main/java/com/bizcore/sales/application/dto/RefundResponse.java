package com.bizcore.sales.application.dto;

import com.bizcore.sales.domain.model.Refund;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RefundResponse(
        UUID id,
        UUID orderId,
        UUID employeeId,
        BigDecimal refundAmount,
        String reason,
        String refundType,
        String refundMethod,
        Instant createdAt
) {
    public static RefundResponse from(Refund r) {
        return new RefundResponse(r.id(), r.orderId(), r.employeeId(),
                r.refundAmount(), r.reason(), r.refundType(), r.refundMethod(), r.createdAt());
    }
}
