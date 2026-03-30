package com.bizcore.catalog.domain.port.out;

import com.bizcore.catalog.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(UUID tenantId, UUID productId);
    Page<Product> findAll(UUID tenantId, UUID categoryId, String search, Pageable pageable);
    boolean existsBySkuAndTenantId(String sku, UUID tenantId);
    void deleteById(UUID tenantId, UUID productId);
}
