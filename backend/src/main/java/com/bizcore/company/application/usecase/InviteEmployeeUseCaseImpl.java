package com.bizcore.company.application.usecase;

import com.bizcore.company.application.dto.EmployeeResponse;
import com.bizcore.company.application.dto.InviteEmployeeRequest;
import com.bizcore.company.domain.model.Company;
import com.bizcore.company.domain.model.EmployeeCreation;
import com.bizcore.company.domain.port.in.InviteEmployeeUseCase;
import com.bizcore.company.domain.port.out.CompanyRepositoryPort;
import com.bizcore.company.domain.port.out.EmployeeRepositoryPort;
import com.bizcore.company.domain.port.out.PasswordEncoderPort;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.shared.exception.ConflictException;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InviteEmployeeUseCaseImpl implements InviteEmployeeUseCase {

    private final EmployeeRepositoryPort employeeRepo;
    private final CompanyRepositoryPort companyRepo;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    public EmployeeResponse invite(UUID tenantId, InviteEmployeeRequest request) {
        if (employeeRepo.existsByEmail(request.email())) {
            throw new ConflictException("El email ya está en uso: " + request.email());
        }

        Company company = companyRepo.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", tenantId));

        int activeCount = employeeRepo.countActiveByTenantId(tenantId);
        if (!company.canAddEmployee(activeCount)) {
            throw new BusinessException(
                    "Límite de empleados alcanzado. Tu plan permite máximo " + company.maxEmployees()
            );
        }

        var employee = employeeRepo.create(new EmployeeCreation(
                tenantId,
                request.email(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                request.phone(),
                request.role()
        ));

        return toResponse(employee);
    }

    static EmployeeResponse toResponse(com.bizcore.company.domain.model.Employee e) {
        return new EmployeeResponse(
                e.id(), e.email(), e.firstName(), e.lastName(),
                e.phone(), e.role(), e.active(), e.createdAt()
        );
    }
}
