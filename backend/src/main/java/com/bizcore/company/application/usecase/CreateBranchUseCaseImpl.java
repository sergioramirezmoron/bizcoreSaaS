package com.bizcore.company.application.usecase;

import com.bizcore.company.application.dto.BranchRequest;
import com.bizcore.company.application.dto.BranchResponse;
import com.bizcore.company.domain.model.Branch;
import com.bizcore.company.domain.model.Company;
import com.bizcore.company.domain.port.in.CreateBranchUseCase;
import com.bizcore.company.domain.port.out.BranchRepositoryPort;
import com.bizcore.company.domain.port.out.CompanyRepositoryPort;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateBranchUseCaseImpl implements CreateBranchUseCase {

    private final BranchRepositoryPort branchRepo;
    private final CompanyRepositoryPort companyRepo;

    @Override
    public BranchResponse create(UUID tenantId, BranchRequest request) {
        Company company = companyRepo.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", tenantId));

        int currentCount = branchRepo.countByTenantId(tenantId);
        if (!company.canAddBranch(currentCount)) {
            throw new BusinessException(
                    "Límite de sucursales alcanzado. Tu plan permite máximo " + company.maxBranches()
            );
        }

        Branch branch = branchRepo.save(new Branch(
                null,
                tenantId,
                request.name(),
                request.phone(),
                request.address(),
                request.main(),
                null
        ));

        return toResponse(branch);
    }

    static BranchResponse toResponse(Branch b) {
        return new BranchResponse(b.id(), b.tenantId(), b.name(), b.phone(), b.address(), b.main(), b.createdAt());
    }
}
