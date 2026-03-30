package com.bizcore.catalog.domain.port.in;

import com.bizcore.catalog.application.dto.ProductResponse;

import java.util.UUID;

public interface GetProductUseCase {
    ProductResponse get(UUID tenantId, UUID productId);
}
