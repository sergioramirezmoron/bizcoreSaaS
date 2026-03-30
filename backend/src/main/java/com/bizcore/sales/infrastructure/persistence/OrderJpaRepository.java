package com.bizcore.sales.infrastructure.persistence;

import com.bizcore.sales.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {

    Optional<OrderJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    @Query("""
            SELECT o FROM OrderJpaEntity o
            WHERE o.tenantId = :tenantId
              AND (:branchId   IS NULL OR o.branchId   = :branchId)
              AND (:customerId IS NULL OR o.customerId = :customerId)
              AND (:status     IS NULL OR o.status     = :status)
            """)
    Page<OrderJpaEntity> search(@Param("tenantId")   UUID tenantId,
                                @Param("branchId")   UUID branchId,
                                @Param("customerId") UUID customerId,
                                @Param("status")     OrderStatus status,
                                Pageable pageable);

    @Query(value = """
            SELECT COUNT(*) FROM orders
            WHERE tenant_id = :tenantId
              AND EXTRACT(YEAR FROM created_at) = :year
            """, nativeQuery = true)
    long countByTenantIdAndYear(@Param("tenantId") UUID tenantId, @Param("year") int year);
}
