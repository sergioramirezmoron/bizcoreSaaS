package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.application.dto.CategoryResponse;
import com.bizcore.catalog.domain.port.in.ListCategoriesUseCase;
import com.bizcore.catalog.domain.port.out.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListCategoriesUseCaseImpl implements ListCategoriesUseCase {

    private final CategoryRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> list(UUID tenantId) {
        return repository.findAllByTenantId(tenantId)
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
