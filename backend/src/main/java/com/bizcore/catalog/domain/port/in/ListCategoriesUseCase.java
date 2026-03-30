package com.bizcore.catalog.domain.port.in;

import com.bizcore.catalog.application.dto.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface ListCategoriesUseCase {
    List<CategoryResponse> list(UUID tenantId);
}
