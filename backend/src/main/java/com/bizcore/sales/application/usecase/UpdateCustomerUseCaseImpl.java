package com.bizcore.sales.application.usecase;

import com.bizcore.sales.application.dto.CustomerRequest;
import com.bizcore.sales.application.dto.CustomerResponse;
import com.bizcore.sales.domain.model.Customer;
import com.bizcore.sales.domain.port.in.UpdateCustomerUseCase;
import com.bizcore.sales.domain.port.out.CustomerRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCustomerUseCaseImpl implements UpdateCustomerUseCase {

    private final CustomerRepositoryPort repository;

    @Override
    @Transactional
    public CustomerResponse update(UUID tenantId, UUID customerId, CustomerRequest request) {
        Customer existing = repository.findById(tenantId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Customer updated = new Customer(
                existing.id(), existing.tenantId(),
                request.firstName(), request.lastName(),
                request.email(), request.phone(), request.notes(),
                existing.totalSpent(), existing.orderCount(), existing.createdAt()
        );
        return CustomerResponse.from(repository.save(updated));
    }
}
