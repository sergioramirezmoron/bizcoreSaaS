package com.bizcore.catalog.domain.port.in;

import java.util.UUID;

public interface DeleteProductVariantUseCase {
    void delete(UUID tenantId, UUID variantId);
}
