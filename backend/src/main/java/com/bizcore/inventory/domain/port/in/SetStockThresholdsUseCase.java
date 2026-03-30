package com.bizcore.inventory.domain.port.in;

import com.bizcore.inventory.application.dto.SetThresholdsRequest;
import com.bizcore.inventory.application.dto.StockItemResponse;

import java.util.UUID;

public interface SetStockThresholdsUseCase {
    StockItemResponse setThresholds(UUID tenantId, UUID stockItemId, SetThresholdsRequest request);
}
