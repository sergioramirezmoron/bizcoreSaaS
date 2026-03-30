package com.bizcore.inventory.domain.port.in;

import com.bizcore.inventory.application.dto.StockMovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListStockMovementsUseCase {
    Page<StockMovementResponse> list(UUID tenantId, UUID stockItemId, Pageable pageable);
}
