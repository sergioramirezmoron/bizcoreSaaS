package com.bizcore.company.application.usecase;

import com.bizcore.company.application.dto.BranchResponse;
import com.bizcore.company.domain.port.in.ListBranchesUseCase;
import com.bizcore.company.domain.port.out.BranchRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListBranchesUseCaseImpl implements ListBranchesUseCase {

    private final BranchRepositoryPort branchRepo;

    @Override
    public List<BranchResponse> list(UUID tenantId) {
        return branchRepo.findAllByTenantId(tenantId).stream()
                .map(CreateBranchUseCaseImpl::toResponse)
                .toList();
    }
}
