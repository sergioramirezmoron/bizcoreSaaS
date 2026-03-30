package com.bizcore.catalog.domain.port.in;

import com.bizcore.catalog.application.dto.CreateProductRequest;
import com.bizcore.catalog.application.dto.ProductResponse;

import java.util.UUID;

public interface CreateProductUseCase {
    ProductResponse create(UUID tenantId, CreateProductRequest request);
}
