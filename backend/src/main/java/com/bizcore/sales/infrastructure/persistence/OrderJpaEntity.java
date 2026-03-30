package com.bizcore.sales.infrastructure.persistence;

import com.bizcore.sales.domain.model.Order;
import com.bizcore.sales.domain.model.OrderStatus;
import com.bizcore.sales.domain.model.PaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(name = "order_number", nullable = false, length = 30)
    private String orderNumber;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30)
    private PaymentMethod paymentMethod;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected OrderJpaEntity() {}

    public static OrderJpaEntity from(Order o) {
        OrderJpaEntity e = new OrderJpaEntity();
        e.id = o.id(); e.tenantId = o.tenantId(); e.branchId = o.branchId();
        e.orderNumber = o.orderNumber(); e.employeeId = o.employeeId(); e.customerId = o.customerId();
        e.status = o.status(); e.subtotal = o.subtotal(); e.discountAmount = o.discountAmount();
        e.taxAmount = o.taxAmount(); e.total = o.total(); e.paymentMethod = o.paymentMethod();
        e.notes = o.notes(); e.completedAt = o.completedAt(); e.createdAt = o.createdAt();
        e.updatedAt = o.updatedAt();
        return e;
    }

    public Order toDomain() {
        return new Order(id, tenantId, branchId, orderNumber, employeeId, customerId, status,
                subtotal, discountAmount, taxAmount, total, paymentMethod, notes,
                completedAt, createdAt, updatedAt);
    }
}
