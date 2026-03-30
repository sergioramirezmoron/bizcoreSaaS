package com.bizcore.sales.infrastructure.persistence;

import com.bizcore.sales.domain.model.OrderItem;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemJpaEntity {

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent;

    @Column(name = "line_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal;

    protected OrderItemJpaEntity() {}

    public static OrderItemJpaEntity from(OrderItem i) {
        OrderItemJpaEntity e = new OrderItemJpaEntity();
        e.id = i.id(); e.orderId = i.orderId(); e.tenantId = i.tenantId();
        e.productId = i.productId(); e.variantId = i.variantId(); e.productName = i.productName();
        e.quantity = i.quantity(); e.unitPrice = i.unitPrice(); e.taxRate = i.taxRate();
        e.discountPercent = i.discountPercent(); e.lineTotal = i.lineTotal();
        return e;
    }

    public OrderItem toDomain() {
        return new OrderItem(id, orderId, tenantId, productId, variantId, productName,
                quantity, unitPrice, taxRate, discountPercent, lineTotal);
    }
}
