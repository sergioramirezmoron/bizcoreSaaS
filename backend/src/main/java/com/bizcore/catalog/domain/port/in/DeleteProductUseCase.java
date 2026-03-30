package com.bizcore.catalog.domain.port.in;

import java.util.UUID;

public interface DeleteProductUseCase {
    void delete(UUID tenantId, UUID productId);
}
