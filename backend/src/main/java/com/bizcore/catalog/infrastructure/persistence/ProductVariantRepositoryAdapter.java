package com.bizcore.catalog.infrastructure.persistence;

import com.bizcore.catalog.domain.model.ProductVariant;
import com.bizcore.catalog.domain.port.out.ProductVariantRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductVariantRepositoryAdapter implements ProductVariantRepositoryPort {

    private final ProductVariantJpaRepository jpaRepository;

    @Override
    public ProductVariant save(ProductVariant variant) {
        return jpaRepository.save(ProductVariantJpaEntity.from(variant)).toDomain();
    }

    @Override
    public Optional<ProductVariant> findById(UUID tenantId, UUID variantId) {
        return jpaRepository.findByIdAndTenantId(variantId, tenantId).map(ProductVariantJpaEntity::toDomain);
    }

    @Override
    public List<ProductVariant> findAllByProductId(UUID tenantId, UUID productId) {
        return jpaRepository.findAllByProductIdAndTenantId(productId, tenantId).stream()
                .map(ProductVariantJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID tenantId, UUID variantId) {
        jpaRepository.findByIdAndTenantId(variantId, tenantId).ifPresent(jpaRepository::delete);
    }
}
