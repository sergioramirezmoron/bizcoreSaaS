package com.bizcore.suppliers.domain.port.out;

import com.bizcore.suppliers.domain.model.Supplier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierRepositoryPort {
    Supplier save(Supplier supplier);
    Optional<Supplier> findByIdAndTenantId(UUID id, UUID tenantId);
    List<Supplier> findAllByTenantId(UUID tenantId);
    boolean existsByIdAndTenantId(UUID id, UUID tenantId);
}
