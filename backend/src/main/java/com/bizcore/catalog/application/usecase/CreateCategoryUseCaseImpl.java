package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.application.dto.CategoryRequest;
import com.bizcore.catalog.application.dto.CategoryResponse;
import com.bizcore.catalog.domain.model.Category;
import com.bizcore.catalog.domain.port.in.CreateCategoryUseCase;
import com.bizcore.catalog.domain.port.out.CategoryRepositoryPort;
import com.bizcore.shared.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCategoryUseCaseImpl implements CreateCategoryUseCase {

    private final CategoryRepositoryPort repository;

    @Override
    @Transactional
    public CategoryResponse create(UUID tenantId, CategoryRequest request) {
        if (repository.existsByNameAndTenantId(request.name(), tenantId)) {
            throw new ConflictException("Category name already exists");
        }
        Category category = new Category(
                UUID.randomUUID(),
                tenantId,
                request.name(),
                request.parentId(),
                request.color(),
                request.sortOrder(),
                true,
                Instant.now()
        );
        return CategoryResponse.from(repository.save(category));
    }
}
