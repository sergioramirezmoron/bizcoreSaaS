package com.bizcore.sales.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface RefundJpaRepository extends JpaRepository<RefundJpaEntity, UUID> {

    List<RefundJpaEntity> findAllByOrderIdAndTenantId(UUID orderId, UUID tenantId);

    @Query("SELECT COALESCE(SUM(r.refundAmount), 0) FROM RefundJpaEntity r WHERE r.orderId = :orderId AND r.tenantId = :tenantId")
    BigDecimal sumRefundedAmount(@Param("orderId") UUID orderId, @Param("tenantId") UUID tenantId);
}
