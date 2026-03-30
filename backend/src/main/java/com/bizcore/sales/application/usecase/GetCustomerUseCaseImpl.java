package com.bizcore.sales.application.usecase;

import com.bizcore.sales.application.dto.CustomerResponse;
import com.bizcore.sales.domain.port.in.GetCustomerUseCase;
import com.bizcore.sales.domain.port.out.CustomerRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCustomerUseCaseImpl implements GetCustomerUseCase {

    private final CustomerRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse get(UUID tenantId, UUID customerId) {
        return repository.findById(tenantId, customerId)
                .map(CustomerResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }
}
