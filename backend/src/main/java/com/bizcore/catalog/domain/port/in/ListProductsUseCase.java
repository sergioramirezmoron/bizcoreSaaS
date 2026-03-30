package com.bizcore.catalog.domain.port.in;

import com.bizcore.catalog.application.dto.ProductSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListProductsUseCase {
    Page<ProductSummaryResponse> list(UUID tenantId, UUID categoryId, String search, Pageable pageable);
}
