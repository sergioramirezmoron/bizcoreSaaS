package com.bizcore.company.domain.port.in;

import java.util.UUID;

public interface DeleteBranchUseCase {
    void delete(UUID tenantId, UUID branchId);
}
