package com.bizcore.inventory.domain.port.in;

import com.bizcore.inventory.application.dto.InitStockItemRequest;
import com.bizcore.inventory.application.dto.StockItemResponse;

import java.util.UUID;

public interface InitStockItemUseCase {
    StockItemResponse init(UUID tenantId, UUID userId, InitStockItemRequest request);
}
