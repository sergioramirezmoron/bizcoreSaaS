package com.bizcore.sales.infrastructure.persistence;

import com.bizcore.sales.domain.model.Customer;
import com.bizcore.sales.domain.port.out.CustomerRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository jpaRepository;

    @Override
    public Customer save(Customer customer) {
        return jpaRepository.save(CustomerJpaEntity.from(customer)).toDomain();
    }

    @Override
    public Optional<Customer> findById(UUID tenantId, UUID customerId) {
        return jpaRepository.findByIdAndTenantId(customerId, tenantId).map(CustomerJpaEntity::toDomain);
    }

    @Override
    public Page<Customer> findAll(UUID tenantId, String search, Pageable pageable) {
        return jpaRepository.search(tenantId, search, pageable).map(CustomerJpaEntity::toDomain);
    }
}
