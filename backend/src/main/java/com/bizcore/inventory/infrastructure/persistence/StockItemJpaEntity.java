package com.bizcore.inventory.infrastructure.persistence;

import com.bizcore.inventory.domain.model.StockItem;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_items")
public class StockItemJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(name = "min_quantity", nullable = false, precision = 10, scale = 3)
    private BigDecimal minQuantity;

    @Column(name = "max_quantity", precision = 10, scale = 3)
    private BigDecimal maxQuantity;

    @Column(length = 100)
    private String location;

    @Column(name = "last_updated_at", nullable = false)
    private Instant lastUpdatedAt;

    protected StockItemJpaEntity() {}

    public static StockItemJpaEntity from(StockItem s) {
        StockItemJpaEntity e = new StockItemJpaEntity();
        e.id = s.id();
        e.tenantId = s.tenantId();
        e.productId = s.productId();
        e.variantId = s.variantId();
        e.branchId = s.branchId();
        e.quantity = s.quantity();
        e.minQuantity = s.minQuantity();
        e.maxQuantity = s.maxQuantity();
        e.location = s.location();
        e.lastUpdatedAt = s.lastUpdatedAt();
        return e;
    }

    public StockItem toDomain() {
        return new StockItem(id, tenantId, productId, variantId, branchId,
                quantity, minQuantity, maxQuantity, location, lastUpdatedAt);
    }
}
