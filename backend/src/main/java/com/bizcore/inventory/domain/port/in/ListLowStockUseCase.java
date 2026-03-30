package com.bizcore.inventory.domain.port.in;

import com.bizcore.inventory.application.dto.StockItemResponse;

import java.util.List;
import java.util.UUID;

public interface ListLowStockUseCase {
    List<StockItemResponse> listLowStock(UUID tenantId, UUID branchId);
}
