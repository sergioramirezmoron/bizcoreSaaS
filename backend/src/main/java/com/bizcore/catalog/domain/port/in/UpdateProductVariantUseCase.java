package com.bizcore.catalog.domain.port.in;

import com.bizcore.catalog.application.dto.ProductVariantRequest;
import com.bizcore.catalog.application.dto.ProductVariantResponse;

import java.util.UUID;

public interface UpdateProductVariantUseCase {
    ProductVariantResponse update(UUID tenantId, UUID variantId, ProductVariantRequest request);
}
