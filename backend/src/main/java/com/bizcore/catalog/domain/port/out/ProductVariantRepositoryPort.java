package com.bizcore.catalog.domain.port.out;

import com.bizcore.catalog.domain.model.ProductVariant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductVariantRepositoryPort {
    ProductVariant save(ProductVariant variant);
    Optional<ProductVariant> findById(UUID tenantId, UUID variantId);
    List<ProductVariant> findAllByProductId(UUID tenantId, UUID productId);
    void deleteById(UUID tenantId, UUID variantId);
}
