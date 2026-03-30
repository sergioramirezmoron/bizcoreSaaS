package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.application.dto.CategoryRequest;
import com.bizcore.catalog.application.dto.CategoryResponse;
import com.bizcore.catalog.domain.model.Category;
import com.bizcore.catalog.domain.port.in.UpdateCategoryUseCase;
import com.bizcore.catalog.domain.port.out.CategoryRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCategoryUseCaseImpl implements UpdateCategoryUseCase {

    private final CategoryRepositoryPort repository;

    @Override
    @Transactional
    public CategoryResponse update(UUID tenantId, UUID categoryId, CategoryRequest request) {
        Category existing = repository.findById(tenantId, categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Category updated = new Category(
                existing.id(),
                existing.tenantId(),
                request.name(),
                request.parentId(),
                request.color(),
                request.sortOrder(),
                existing.active(),
                existing.createdAt()
        );
        return CategoryResponse.from(repository.save(updated));
    }
}
