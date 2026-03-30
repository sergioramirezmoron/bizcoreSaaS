package com.bizcore.inventory.domain.port.out;

import com.bizcore.inventory.domain.model.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StockMovementRepositoryPort {
    StockMovement save(StockMovement movement);
    Page<StockMovement> findAllByStockItemId(UUID tenantId, UUID stockItemId, Pageable pageable);
}
