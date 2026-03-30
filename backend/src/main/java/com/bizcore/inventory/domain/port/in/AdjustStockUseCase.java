package com.bizcore.inventory.domain.port.in;

import com.bizcore.inventory.application.dto.AdjustStockRequest;
import com.bizcore.inventory.application.dto.StockMovementResponse;

import java.util.UUID;

public interface AdjustStockUseCase {
    StockMovementResponse adjust(UUID tenantId, UUID stockItemId, UUID userId, AdjustStockRequest request);
}
