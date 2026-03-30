package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.application.dto.ProductSummaryResponse;
import com.bizcore.catalog.domain.port.in.ListProductsUseCase;
import com.bizcore.catalog.domain.port.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListProductsUseCaseImpl implements ListProductsUseCase {

    private final ProductRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> list(UUID tenantId, UUID categoryId, String search, Pageable pageable) {
        return repository.findAll(tenantId, categoryId, search, pageable)
                .map(ProductSummaryResponse::from);
    }
}
