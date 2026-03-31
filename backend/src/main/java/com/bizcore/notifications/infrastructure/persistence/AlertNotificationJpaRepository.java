package com.bizcore.notifications.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AlertNotificationJpaRepository extends JpaRepository<AlertNotificationJpaEntity, UUID> {

    Page<AlertNotificationJpaEntity> findByTenantIdOrderBySentAtDesc(UUID tenantId, Pageable pageable);

    long countByTenantIdAndReadFalse(UUID tenantId);

    @Modifying
    @Query("UPDATE AlertNotificationJpaEntity n SET n.read = true WHERE n.tenantId = :tenantId AND n.read = false")
    void markAllReadForTenant(@Param("tenantId") UUID tenantId);
}
