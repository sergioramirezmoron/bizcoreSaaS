package com.bizcore.shared.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, UUID> {

    Page<AuditLogJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId, Pageable pageable);

    Page<AuditLogJpaEntity> findByTenantIdAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
            UUID tenantId, String entityType, UUID entityId, Pageable pageable);
}
