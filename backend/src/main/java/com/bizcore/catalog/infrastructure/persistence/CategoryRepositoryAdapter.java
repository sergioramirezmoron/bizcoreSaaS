package com.bizcore.catalog.infrastructure.persistence;

import com.bizcore.catalog.domain.model.Category;
import com.bizcore.catalog.domain.port.out.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository jpaRepository;

    @Override
    public Category save(Category category) {
        return jpaRepository.save(CategoryJpaEntity.from(category)).toDomain();
    }

    @Override
    public Optional<Category> findById(UUID tenantId, UUID categoryId) {
        return jpaRepository.findByIdAndTenantId(categoryId, tenantId).map(CategoryJpaEntity::toDomain);
    }

    @Override
    public List<Category> findAllByTenantId(UUID tenantId) {
        return jpaRepository.findAllByTenantId(tenantId).stream()
                .map(CategoryJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByNameAndTenantId(String name, UUID tenantId) {
        return jpaRepository.existsByNameAndTenantId(name, tenantId);
    }

    @Override
    public void deleteById(UUID tenantId, UUID categoryId) {
        jpaRepository.findByIdAndTenantId(categoryId, tenantId).ifPresent(jpaRepository::delete);
    }
}
