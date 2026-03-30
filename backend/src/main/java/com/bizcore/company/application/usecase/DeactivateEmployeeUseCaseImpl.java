package com.bizcore.company.application.usecase;

import com.bizcore.company.domain.model.Employee;
import com.bizcore.company.domain.port.in.DeactivateEmployeeUseCase;
import com.bizcore.company.domain.port.out.EmployeeRepositoryPort;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeactivateEmployeeUseCaseImpl implements DeactivateEmployeeUseCase {

    private final EmployeeRepositoryPort employeeRepo;

    @Override
    public void deactivate(UUID tenantId, UUID employeeId) {
        Employee employee = employeeRepo.findByIdAndTenantId(employeeId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));

        // No se puede desactivar a uno mismo
        UUID currentUserId = (UUID) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (employeeId.equals(currentUserId)) {
            throw new BusinessException("No puedes desactivar tu propia cuenta");
        }

        Employee deactivated = new Employee(
                employee.id(), employee.tenantId(), employee.email(),
                employee.firstName(), employee.lastName(), employee.phone(),
                employee.role(), false, employee.createdAt()
        );
        employeeRepo.save(deactivated);
    }
}
