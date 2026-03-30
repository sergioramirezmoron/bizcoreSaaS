package com.bizcore.company.domain.port.out;

import com.bizcore.company.domain.model.Branch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepositoryPort {
    Branch save(Branch branch);
    Optional<Branch> findByIdAndTenantId(UUID id, UUID tenantId);
    List<Branch> findAllByTenantId(UUID tenantId);
    int countByTenantId(UUID tenantId);
    void delete(Branch branch);
}
