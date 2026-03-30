package com.bizcore.inventory.domain.port.in;

import com.bizcore.inventory.application.dto.StockItemResponse;

import java.util.UUID;

public interface GetStockItemUseCase {
    StockItemResponse get(UUID tenantId, UUID stockItemId);
}
