package com.bizcore.catalog.infrastructure.persistence;

import com.bizcore.catalog.domain.model.ProductImage;
import com.bizcore.catalog.domain.port.out.ProductImageRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductImageRepositoryAdapter implements ProductImageRepositoryPort {

    private final ProductImageJpaRepository jpaRepository;

    @Override
    public ProductImage save(ProductImage image) {
        return jpaRepository.save(ProductImageJpaEntity.from(image)).toDomain();
    }

    @Override
    public Optional<ProductImage> findById(UUID tenantId, UUID imageId) {
        return jpaRepository.findByIdAndTenantId(imageId, tenantId).map(ProductImageJpaEntity::toDomain);
    }

    @Override
    public List<ProductImage> findAllByProductId(UUID tenantId, UUID productId) {
        return jpaRepository.findAllByProductIdAndTenantId(productId, tenantId).stream()
                .map(ProductImageJpaEntity::toDomain)
                .toList();
    }

    @Override
    public int countByProductId(UUID tenantId, UUID productId) {
        return jpaRepository.countByProductIdAndTenantId(productId, tenantId);
    }

    @Override
    public void deleteById(UUID tenantId, UUID imageId) {
        jpaRepository.findByIdAndTenantId(imageId, tenantId).ifPresent(jpaRepository::delete);
    }
}
