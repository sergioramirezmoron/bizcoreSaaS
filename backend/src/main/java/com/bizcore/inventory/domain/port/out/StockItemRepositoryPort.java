package com.bizcore.inventory.domain.port.out;

import com.bizcore.inventory.domain.model.StockItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockItemRepositoryPort {
    StockItem save(StockItem stockItem);
    Optional<StockItem> findById(UUID tenantId, UUID stockItemId);
    Page<StockItem> findAll(UUID tenantId, UUID productId, UUID variantId, UUID branchId, Pageable pageable);
    List<StockItem> findLowStock(UUID tenantId, UUID branchId);
    boolean existsByKey(UUID tenantId, UUID productId, UUID variantId, UUID branchId);
}
