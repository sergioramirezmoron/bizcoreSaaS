package com.bizcore.company.application.usecase;

import com.bizcore.company.application.dto.EmployeeResponse;
import com.bizcore.company.domain.port.in.ListEmployeesUseCase;
import com.bizcore.company.domain.port.out.EmployeeRepositoryPort;
import com.bizcore.shared.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListEmployeesUseCaseImpl implements ListEmployeesUseCase {

    private final EmployeeRepositoryPort employeeRepo;

    @Override
    public PageResponse<EmployeeResponse> list(UUID tenantId, Pageable pageable) {
        return PageResponse.from(
                employeeRepo.findAllByTenantId(tenantId, pageable)
                        .map(InviteEmployeeUseCaseImpl::toResponse)
        );
    }
}
