package com.bizcore.sales.application.usecase;

import com.bizcore.sales.application.dto.CustomerRequest;
import com.bizcore.sales.application.dto.CustomerResponse;
import com.bizcore.sales.domain.model.Customer;
import com.bizcore.sales.domain.port.in.CreateCustomerUseCase;
import com.bizcore.sales.domain.port.out.CustomerRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCustomerUseCaseImpl implements CreateCustomerUseCase {

    private final CustomerRepositoryPort repository;

    @Override
    @Transactional
    public CustomerResponse create(UUID tenantId, CustomerRequest request) {
        Customer customer = new Customer(
                UUID.randomUUID(), tenantId,
                request.firstName(), request.lastName(),
                request.email(), request.phone(), request.notes(),
                BigDecimal.ZERO, 0, Instant.now()
        );
        return CustomerResponse.from(repository.save(customer));
    }
}
