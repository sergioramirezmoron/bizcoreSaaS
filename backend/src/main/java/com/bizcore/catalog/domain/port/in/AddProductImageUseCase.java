package com.bizcore.catalog.domain.port.in;

import com.bizcore.catalog.application.dto.ProductImageResponse;

import java.io.InputStream;
import java.util.UUID;

public interface AddProductImageUseCase {
    ProductImageResponse add(UUID tenantId, UUID productId, String filename, String contentType, long sizeBytes, InputStream content);
}
