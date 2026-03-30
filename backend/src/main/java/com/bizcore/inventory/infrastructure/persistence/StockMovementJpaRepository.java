package com.bizcore.inventory.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StockMovementJpaRepository extends JpaRepository<StockMovementJpaEntity, UUID> {
    Page<StockMovementJpaEntity> findAllByStockItemIdAndTenantId(UUID stockItemId, UUID tenantId, Pageable pageable);
}
