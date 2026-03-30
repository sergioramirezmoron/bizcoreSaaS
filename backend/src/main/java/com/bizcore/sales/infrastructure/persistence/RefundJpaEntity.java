package com.bizcore.sales.infrastructure.persistence;

import com.bizcore.sales.domain.model.Refund;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refunds")
public class RefundJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "employee_id")
    private UUID employeeId;

    @Column(name = "refund_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(columnDefinition = "text")
    private String reason;

    @Column(name = "refund_type", length = 20)
    private String refundType;

    @Column(name = "refund_method", length = 30)
    private String refundMethod;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected RefundJpaEntity() {}

    public static RefundJpaEntity from(Refund r) {
        RefundJpaEntity e = new RefundJpaEntity();
        e.id = r.id(); e.tenantId = r.tenantId(); e.orderId = r.orderId();
        e.employeeId = r.employeeId(); e.refundAmount = r.refundAmount();
        e.reason = r.reason(); e.refundType = r.refundType();
        e.refundMethod = r.refundMethod(); e.createdAt = r.createdAt();
        return e;
    }

    public Refund toDomain() {
        return new Refund(id, tenantId, orderId, employeeId, refundAmount,
                reason, refundType, refundMethod, createdAt);
    }
}
