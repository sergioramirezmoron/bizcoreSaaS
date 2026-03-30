package com.bizcore.notifications.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationLogJpaRepository extends JpaRepository<NotificationLogJpaEntity, UUID> {
    Page<NotificationLogJpaEntity> findByTenantId(UUID tenantId, Pageable pageable);
}
