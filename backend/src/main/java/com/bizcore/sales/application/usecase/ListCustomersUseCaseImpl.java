package com.bizcore.sales.application.usecase;

import com.bizcore.sales.application.dto.CustomerResponse;
import com.bizcore.sales.domain.port.in.ListCustomersUseCase;
import com.bizcore.sales.domain.port.out.CustomerRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListCustomersUseCaseImpl implements ListCustomersUseCase {

    private final CustomerRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> list(UUID tenantId, String search, Pageable pageable) {
        return repository.findAll(tenantId, search, pageable).map(CustomerResponse::from);
    }
}
