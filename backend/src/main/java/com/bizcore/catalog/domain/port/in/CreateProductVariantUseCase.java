package com.bizcore.catalog.domain.port.in;

import com.bizcore.catalog.application.dto.ProductVariantRequest;
import com.bizcore.catalog.application.dto.ProductVariantResponse;

import java.util.UUID;

public interface CreateProductVariantUseCase {
    ProductVariantResponse create(UUID tenantId, UUID productId, ProductVariantRequest request);
}
