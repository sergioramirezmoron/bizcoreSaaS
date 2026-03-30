package com.bizcore.inventory.infrastructure.persistence;

import com.bizcore.inventory.domain.model.MovementType;
import com.bizcore.inventory.domain.model.StockMovement;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
public class StockMovementJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "stock_item_id", nullable = false)
    private UUID stockItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private MovementType movementType;

    @Column(name = "quantity_change", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantityChange;

    @Column(name = "quantity_before", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantityBefore;

    @Column(name = "quantity_after", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantityAfter;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected StockMovementJpaEntity() {}

    public static StockMovementJpaEntity from(StockMovement m) {
        StockMovementJpaEntity e = new StockMovementJpaEntity();
        e.id = m.id();
        e.tenantId = m.tenantId();
        e.stockItemId = m.stockItemId();
        e.movementType = m.movementType();
        e.quantityChange = m.quantityChange();
        e.quantityBefore = m.quantityBefore();
        e.quantityAfter = m.quantityAfter();
        e.referenceId = m.referenceId();
        e.notes = m.notes();
        e.createdBy = m.createdBy();
        e.createdAt = m.createdAt();
        return e;
    }

    public StockMovement toDomain() {
        return new StockMovement(id, tenantId, stockItemId, movementType,
                quantityChange, quantityBefore, quantityAfter,
                referenceId, notes, createdBy, createdAt);
    }
}
