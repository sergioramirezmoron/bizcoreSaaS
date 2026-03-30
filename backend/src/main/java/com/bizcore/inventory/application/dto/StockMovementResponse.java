package com.bizcore.inventory.application.dto;

import com.bizcore.inventory.domain.model.MovementType;
import com.bizcore.inventory.domain.model.StockMovement;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        UUID stockItemId,
        MovementType movementType,
        BigDecimal quantityChange,
        BigDecimal quantityBefore,
        BigDecimal quantityAfter,
        UUID referenceId,
        String notes,
        UUID createdBy,
        Instant createdAt
) {
    public static StockMovementResponse from(StockMovement m) {
        return new StockMovementResponse(
                m.id(), m.stockItemId(), m.movementType(),
                m.quantityChange(), m.quantityBefore(), m.quantityAfter(),
                m.referenceId(), m.notes(), m.createdBy(), m.createdAt()
        );
    }
}
