package com.bizcore.catalog.domain.port.out;

import com.bizcore.catalog.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {
    Category save(Category category);
    Optional<Category> findById(UUID tenantId, UUID categoryId);
    List<Category> findAllByTenantId(UUID tenantId);
    boolean existsByNameAndTenantId(String name, UUID tenantId);
    void deleteById(UUID tenantId, UUID categoryId);
}
