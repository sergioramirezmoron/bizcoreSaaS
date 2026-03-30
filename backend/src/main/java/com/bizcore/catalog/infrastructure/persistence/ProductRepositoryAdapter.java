package com.bizcore.catalog.infrastructure.persistence;

import com.bizcore.catalog.domain.model.Product;
import com.bizcore.catalog.domain.port.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository jpaRepository;

    @Override
    public Product save(Product product) {
        return jpaRepository.save(ProductJpaEntity.from(product)).toDomain();
    }

    @Override
    public Optional<Product> findById(UUID tenantId, UUID productId) {
        return jpaRepository.findByIdAndTenantId(productId, tenantId).map(ProductJpaEntity::toDomain);
    }

    @Override
    public Page<Product> findAll(UUID tenantId, UUID categoryId, String search, Pageable pageable) {
        return jpaRepository.search(tenantId, categoryId, search, pageable).map(ProductJpaEntity::toDomain);
    }

    @Override
    public boolean existsBySkuAndTenantId(String sku, UUID tenantId) {
        return jpaRepository.existsBySkuAndTenantId(sku, tenantId);
    }

    @Override
    public void deleteById(UUID tenantId, UUID productId) {
        jpaRepository.findByIdAndTenantId(productId, tenantId).ifPresent(jpaRepository::delete);
    }
}
