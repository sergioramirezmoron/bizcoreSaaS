package com.bizcore.sales.domain.port.out;

import com.bizcore.sales.domain.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepositoryPort {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID tenantId, UUID customerId);
    Page<Customer> findAll(UUID tenantId, String search, Pageable pageable);
}
