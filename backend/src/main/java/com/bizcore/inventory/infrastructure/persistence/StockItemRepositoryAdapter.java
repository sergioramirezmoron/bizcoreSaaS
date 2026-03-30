package com.bizcore.inventory.infrastructure.persistence;

import com.bizcore.inventory.domain.model.StockItem;
import com.bizcore.inventory.domain.port.out.StockItemRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StockItemRepositoryAdapter implements StockItemRepositoryPort {

    private final StockItemJpaRepository jpaRepository;

    @Override
    public StockItem save(StockItem stockItem) {
        return jpaRepository.save(StockItemJpaEntity.from(stockItem)).toDomain();
    }

    @Override
    public Optional<StockItem> findById(UUID tenantId, UUID stockItemId) {
        return jpaRepository.findByIdAndTenantId(stockItemId, tenantId).map(StockItemJpaEntity::toDomain);
    }

    @Override
    public Page<StockItem> findAll(UUID tenantId, UUID productId, UUID variantId, UUID branchId, Pageable pageable) {
        return jpaRepository.search(tenantId, productId, variantId, branchId, pageable)
                .map(StockItemJpaEntity::toDomain);
    }

    @Override
    public List<StockItem> findLowStock(UUID tenantId, UUID branchId) {
        return jpaRepository.findLowStock(tenantId, branchId).stream()
                .map(StockItemJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByKey(UUID tenantId, UUID productId, UUID variantId, UUID branchId) {
        return jpaRepository.existsByKey(tenantId, productId, variantId, branchId);
    }
}
