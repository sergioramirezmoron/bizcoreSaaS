package com.bizcore.company.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchJpaRepository extends JpaRepository<BranchJpaEntity, UUID> {
    Optional<BranchJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);
    List<BranchJpaEntity> findAllByTenantId(UUID tenantId);
    int countByTenantId(UUID tenantId);
}
