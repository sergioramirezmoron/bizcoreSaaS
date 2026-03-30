package com.bizcore.inventory.domain.port.in;

import com.bizcore.inventory.application.dto.StockItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListStockItemsUseCase {
    Page<StockItemResponse> list(UUID tenantId, UUID productId, UUID variantId, UUID branchId, Pageable pageable);
}
