package com.bizcore.catalog.domain.port.in;

import com.bizcore.catalog.application.dto.ProductResponse;
import com.bizcore.catalog.application.dto.UpdateProductRequest;

import java.util.UUID;

public interface UpdateProductUseCase {
    ProductResponse update(UUID tenantId, UUID productId, UpdateProductRequest request);
}
