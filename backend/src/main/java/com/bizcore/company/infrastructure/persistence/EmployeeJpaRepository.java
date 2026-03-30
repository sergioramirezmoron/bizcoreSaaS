package com.bizcore.company.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeJpaEntity, UUID> {
    Optional<EmployeeJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);
    boolean existsByEmail(String email);
    Page<EmployeeJpaEntity> findAllByTenantId(UUID tenantId, Pageable pageable);

    @Query("SELECT COUNT(e) FROM EmployeeJpaEntity e WHERE e.tenantId = :tenantId AND e.active = true")
    int countActiveByTenantId(UUID tenantId);
}
