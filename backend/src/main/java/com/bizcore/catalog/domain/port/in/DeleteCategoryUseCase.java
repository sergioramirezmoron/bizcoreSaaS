package com.bizcore.catalog.domain.port.in;

import java.util.UUID;

public interface DeleteCategoryUseCase {
    void delete(UUID tenantId, UUID categoryId);
}
