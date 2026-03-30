package com.bizcore.inventory.infrastructure.persistence;

import com.bizcore.inventory.domain.model.StockMovement;
import com.bizcore.inventory.domain.port.out.StockMovementRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StockMovementRepositoryAdapter implements StockMovementRepositoryPort {

    private final StockMovementJpaRepository jpaRepository;

    @Override
    public StockMovement save(StockMovement movement) {
        return jpaRepository.save(StockMovementJpaEntity.from(movement)).toDomain();
    }

    @Override
    public Page<StockMovement> findAllByStockItemId(UUID tenantId, UUID stockItemId, Pageable pageable) {
        return jpaRepository.findAllByStockItemIdAndTenantId(stockItemId, tenantId, pageable)
                .map(StockMovementJpaEntity::toDomain);
    }
}
