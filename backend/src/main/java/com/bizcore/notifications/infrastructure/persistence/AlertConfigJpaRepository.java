package com.bizcore.notifications.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertConfigJpaRepository extends JpaRepository<AlertConfigJpaEntity, UUID> {

    List<AlertConfigJpaEntity> findByTenantId(UUID tenantId);

    Optional<AlertConfigJpaEntity> findByTenantIdAndAlertType(UUID tenantId, String alertType);
}
