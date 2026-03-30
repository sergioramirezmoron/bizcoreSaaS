package com.bizcore.company.application.usecase;

import com.bizcore.company.application.dto.BranchRequest;
import com.bizcore.company.application.dto.BranchResponse;
import com.bizcore.company.domain.model.Branch;
import com.bizcore.company.domain.port.in.UpdateBranchUseCase;
import com.bizcore.company.domain.port.out.BranchRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateBranchUseCaseImpl implements UpdateBranchUseCase {

    private final BranchRepositoryPort branchRepo;

    @Override
    public BranchResponse update(UUID tenantId, UUID branchId, BranchRequest request) {
        Branch existing = branchRepo.findByIdAndTenantId(branchId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", branchId));

        Branch updated = new Branch(
                existing.id(),
                existing.tenantId(),
                request.name() != null ? request.name() : existing.name(),
                request.phone() != null ? request.phone() : existing.phone(),
                request.address() != null ? request.address() : existing.address(),
                request.main(),
                existing.createdAt()
        );

        return CreateBranchUseCaseImpl.toResponse(branchRepo.save(updated));
    }
}
