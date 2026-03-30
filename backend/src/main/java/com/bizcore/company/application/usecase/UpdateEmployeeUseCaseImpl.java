package com.bizcore.company.application.usecase;

import com.bizcore.company.application.dto.EmployeeResponse;
import com.bizcore.company.application.dto.UpdateEmployeeRequest;
import com.bizcore.company.domain.model.Employee;
import com.bizcore.company.domain.port.in.UpdateEmployeeUseCase;
import com.bizcore.company.domain.port.out.EmployeeRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateEmployeeUseCaseImpl implements UpdateEmployeeUseCase {

    private final EmployeeRepositoryPort employeeRepo;

    @Override
    public EmployeeResponse update(UUID tenantId, UUID employeeId, UpdateEmployeeRequest request) {
        Employee existing = employeeRepo.findByIdAndTenantId(employeeId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));

        Employee updated = new Employee(
                existing.id(),
                existing.tenantId(),
                existing.email(),
                request.firstName() != null ? request.firstName() : existing.firstName(),
                request.lastName() != null ? request.lastName() : existing.lastName(),
                request.phone() != null ? request.phone() : existing.phone(),
                request.role() != null ? request.role() : existing.role(),
                existing.active(),
                existing.createdAt()
        );

        return InviteEmployeeUseCaseImpl.toResponse(employeeRepo.save(updated));
    }
}
