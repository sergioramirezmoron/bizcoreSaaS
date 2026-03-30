package com.bizcore.catalog.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID> {

    Optional<ProductJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsBySkuAndTenantId(String sku, UUID tenantId);

    @Query("""
            SELECT p FROM ProductJpaEntity p
            WHERE p.tenantId = :tenantId
              AND (:categoryId IS NULL OR p.categoryId = :categoryId)
              AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<ProductJpaEntity> search(
            @Param("tenantId") UUID tenantId,
            @Param("categoryId") UUID categoryId,
            @Param("search") String search,
            Pageable pageable
    );
}
