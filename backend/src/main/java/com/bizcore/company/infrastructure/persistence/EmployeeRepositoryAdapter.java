package com.bizcore.company.infrastructure.persistence;

import com.bizcore.company.domain.model.Employee;
import com.bizcore.company.domain.model.EmployeeCreation;
import com.bizcore.company.domain.port.out.EmployeeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryAdapter implements EmployeeRepositoryPort {

    private final EmployeeJpaRepository jpaRepository;

    @Override
    public Employee create(EmployeeCreation creation) {
        EmployeeJpaEntity entity = EmployeeJpaEntity.builder()
                .tenantId(creation.tenantId())
                .email(creation.email())
                .passwordHash(creation.passwordHash())
                .firstName(creation.firstName())
                .lastName(creation.lastName())
                .phone(creation.phone())
                .role(creation.role())
                .active(true)
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Employee save(Employee employee) {
        EmployeeJpaEntity entity = jpaRepository.findById(employee.id())
                .orElseThrow(() -> new IllegalStateException("Employee not found: " + employee.id()));
        entity.setFirstName(employee.firstName());
        entity.setLastName(employee.lastName());
        entity.setPhone(employee.phone());
        entity.setRole(employee.role());
        entity.setActive(employee.active());
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Employee> findByIdAndTenantId(UUID id, UUID tenantId) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Page<Employee> findAllByTenantId(UUID tenantId, Pageable pageable) {
        return jpaRepository.findAllByTenantId(tenantId, pageable).map(this::toDomain);
    }

    @Override
    public int countActiveByTenantId(UUID tenantId) {
        return jpaRepository.countActiveByTenantId(tenantId);
    }

    private Employee toDomain(EmployeeJpaEntity e) {
        return new Employee(
                e.getId(), e.getTenantId(), e.getEmail(),
                e.getFirstName(), e.getLastName(), e.getPhone(),
                e.getRole(), e.isActive(), e.getCreatedAt()
        );
    }
}
