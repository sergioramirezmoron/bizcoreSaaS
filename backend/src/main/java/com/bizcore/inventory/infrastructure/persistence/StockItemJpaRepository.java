package com.bizcore.inventory.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockItemJpaRepository extends JpaRepository<StockItemJpaEntity, UUID> {

    Optional<StockItemJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    @Query("""
            SELECT s FROM StockItemJpaEntity s
            WHERE s.tenantId = :tenantId
              AND (:productId IS NULL OR s.productId = :productId)
              AND (:variantId IS NULL OR s.variantId = :variantId)
              AND (:branchId  IS NULL OR s.branchId  = :branchId)
            """)
    Page<StockItemJpaEntity> search(
            @Param("tenantId")  UUID tenantId,
            @Param("productId") UUID productId,
            @Param("variantId") UUID variantId,
            @Param("branchId")  UUID branchId,
            Pageable pageable
    );

    @Query("""
            SELECT s FROM StockItemJpaEntity s
            WHERE s.tenantId = :tenantId
              AND (:branchId IS NULL OR s.branchId = :branchId)
              AND s.quantity <= s.minQuantity
            """)
    List<StockItemJpaEntity> findLowStock(
            @Param("tenantId") UUID tenantId,
            @Param("branchId") UUID branchId
    );

    @Query("""
            SELECT s FROM StockItemJpaEntity s
            WHERE s.tenantId = :tenantId
              AND ((:productId IS NULL AND s.productId IS NULL) OR s.productId = :productId)
              AND ((:variantId IS NULL AND s.variantId IS NULL) OR s.variantId = :variantId)
              AND ((:branchId  IS NULL AND s.branchId  IS NULL) OR s.branchId  = :branchId)
            """)
    java.util.Optional<StockItemJpaEntity> findByKey(
            @Param("tenantId")  UUID tenantId,
            @Param("productId") UUID productId,
            @Param("variantId") UUID variantId,
            @Param("branchId")  UUID branchId
    );

    @Query("""
            SELECT COUNT(s) > 0 FROM StockItemJpaEntity s
            WHERE s.tenantId = :tenantId
              AND ((:productId IS NULL AND s.productId IS NULL) OR s.productId = :productId)
              AND ((:variantId IS NULL AND s.variantId IS NULL) OR s.variantId = :variantId)
              AND ((:branchId  IS NULL AND s.branchId  IS NULL) OR s.branchId  = :branchId)
            """)
    boolean existsByKey(
            @Param("tenantId")  UUID tenantId,
            @Param("productId") UUID productId,
            @Param("variantId") UUID variantId,
            @Param("branchId")  UUID branchId
    );
}
