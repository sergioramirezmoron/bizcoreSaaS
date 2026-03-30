package com.bizcore.catalog.domain.port.in;

import com.bizcore.catalog.application.dto.CategoryRequest;
import com.bizcore.catalog.application.dto.CategoryResponse;

import java.util.UUID;

public interface CreateCategoryUseCase {
    CategoryResponse create(UUID tenantId, CategoryRequest request);
}
