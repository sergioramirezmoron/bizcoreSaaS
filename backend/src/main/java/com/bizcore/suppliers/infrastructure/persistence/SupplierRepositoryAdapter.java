package com.bizcore.suppliers.infrastructure.persistence;

import com.bizcore.suppliers.domain.model.Supplier;
import com.bizcore.suppliers.domain.port.out.SupplierRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SupplierRepositoryAdapter implements SupplierRepositoryPort {

    private final SupplierJpaRepository jpa;

    @Override
    public Supplier save(Supplier supplier) {
        return jpa.save(SupplierJpaEntity.from(supplier)).toDomain();
    }

    @Override
    public Optional<Supplier> findByIdAndTenantId(UUID id, UUID tenantId) {
        return jpa.findByIdAndTenantId(id, tenantId).map(SupplierJpaEntity::toDomain);
    }

    @Override
    public List<Supplier> findAllByTenantId(UUID tenantId) {
        return jpa.findAllByTenantIdAndActiveTrue(tenantId)
                .stream()
                .map(SupplierJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByIdAndTenantId(UUID id, UUID tenantId) {
        return jpa.existsByIdAndTenantId(id, tenantId);
    }
}
