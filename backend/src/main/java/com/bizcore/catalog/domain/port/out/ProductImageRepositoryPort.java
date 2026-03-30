package com.bizcore.catalog.domain.port.out;

import com.bizcore.catalog.domain.model.ProductImage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductImageRepositoryPort {
    ProductImage save(ProductImage image);
    Optional<ProductImage> findById(UUID tenantId, UUID imageId);
    List<ProductImage> findAllByProductId(UUID tenantId, UUID productId);
    int countByProductId(UUID tenantId, UUID productId);
    void deleteById(UUID tenantId, UUID imageId);
}
