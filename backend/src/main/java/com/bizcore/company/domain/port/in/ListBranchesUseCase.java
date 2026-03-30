package com.bizcore.company.domain.port.in;

import com.bizcore.company.application.dto.BranchResponse;

import java.util.List;
import java.util.UUID;

public interface ListBranchesUseCase {
    List<BranchResponse> list(UUID tenantId);
}
