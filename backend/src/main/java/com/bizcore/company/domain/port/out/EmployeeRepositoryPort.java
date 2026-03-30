package com.bizcore.company.domain.port.out;

import com.bizcore.company.domain.model.Employee;
import com.bizcore.company.domain.model.EmployeeCreation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepositoryPort {
    Employee create(EmployeeCreation creation);
    Employee save(Employee employee);
    Optional<Employee> findByIdAndTenantId(UUID id, UUID tenantId);
    boolean existsByEmail(String email);
    Page<Employee> findAllByTenantId(UUID tenantId, Pageable pageable);
    int countActiveByTenantId(UUID tenantId);
}
