package com.bizcore.inventory.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StockMovement(
        UUID id,
        UUID tenantId,
        UUID stockItemId,
        MovementType movementType,
        BigDecimal quantityChange,   // positivo = entrada, negativo = salida
        BigDecimal quantityBefore,
        BigDecimal quantityAfter,
        UUID referenceId,            // null para ajustes manuales
        String notes,
        UUID createdBy,
        Instant createdAt
) {}
