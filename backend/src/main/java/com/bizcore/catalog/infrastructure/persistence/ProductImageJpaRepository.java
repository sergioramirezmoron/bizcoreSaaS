package com.bizcore.catalog.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductImageJpaRepository extends JpaRepository<ProductImageJpaEntity, UUID> {
    List<ProductImageJpaEntity> findAllByProductIdAndTenantId(UUID productId, UUID tenantId);
    Optional<ProductImageJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);
    int countByProductIdAndTenantId(UUID productId, UUID tenantId);
}
