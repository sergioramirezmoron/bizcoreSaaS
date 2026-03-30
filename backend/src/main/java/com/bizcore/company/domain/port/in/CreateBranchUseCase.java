package com.bizcore.company.domain.port.in;

import com.bizcore.company.application.dto.BranchRequest;
import com.bizcore.company.application.dto.BranchResponse;

import java.util.UUID;

public interface CreateBranchUseCase {
    BranchResponse create(UUID tenantId, BranchRequest request);
}
