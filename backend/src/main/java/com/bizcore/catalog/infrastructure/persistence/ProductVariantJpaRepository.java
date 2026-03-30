package com.bizcore.catalog.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductVariantJpaRepository extends JpaRepository<ProductVariantJpaEntity, UUID> {
    List<ProductVariantJpaEntity> findAllByProductIdAndTenantId(UUID productId, UUID tenantId);
    Optional<ProductVariantJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);
}
