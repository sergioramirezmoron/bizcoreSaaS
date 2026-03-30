package com.bizcore.sales.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity, UUID> {

    List<OrderItemJpaEntity> findAllByOrderIdAndTenantId(UUID orderId, UUID tenantId);

    @Modifying
    @Query("DELETE FROM OrderItemJpaEntity i WHERE i.orderId = :orderId AND i.tenantId = :tenantId")
    void deleteAllByOrderIdAndTenantId(@Param("orderId") UUID orderId, @Param("tenantId") UUID tenantId);
}
