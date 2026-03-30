package com.bizcore.catalog.domain.port.in;

import java.util.UUID;

public interface DeleteProductImageUseCase {
    void delete(UUID tenantId, UUID imageId);
}
