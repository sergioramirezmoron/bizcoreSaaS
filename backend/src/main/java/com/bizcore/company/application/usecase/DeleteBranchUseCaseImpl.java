package com.bizcore.company.application.usecase;

import com.bizcore.company.domain.model.Branch;
import com.bizcore.company.domain.port.in.DeleteBranchUseCase;
import com.bizcore.company.domain.port.out.BranchRepositoryPort;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteBranchUseCaseImpl implements DeleteBranchUseCase {

    private final BranchRepositoryPort branchRepo;

    @Override
    public void delete(UUID tenantId, UUID branchId) {
        Branch branch = branchRepo.findByIdAndTenantId(branchId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", branchId));

        if (branch.main()) {
            throw new BusinessException("No se puede eliminar la sucursal principal");
        }

        branchRepo.delete(branch);
    }
}
