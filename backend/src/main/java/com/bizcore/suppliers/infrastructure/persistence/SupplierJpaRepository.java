package com.bizcore.suppliers.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierJpaRepository extends JpaRepository<SupplierJpaEntity, UUID> {

    Optional<SupplierJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    List<SupplierJpaEntity> findAllByTenantIdAndActiveTrue(UUID tenantId);

    boolean existsByIdAndTenantId(UUID id, UUID tenantId);
}
