package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.domain.port.in.DeleteCategoryUseCase;
import com.bizcore.catalog.domain.port.out.CategoryRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCategoryUseCaseImpl implements DeleteCategoryUseCase {

    private final CategoryRepositoryPort repository;

    @Override
    @Transactional
    public void delete(UUID tenantId, UUID categoryId) {
        if (repository.findById(tenantId, categoryId).isEmpty()) {
            throw new ResourceNotFoundException("Category not found");
        }
        repository.deleteById(tenantId, categoryId);
    }
}
