package com.bizcore.sales.application.usecase;

import com.bizcore.sales.application.dto.OrderSummaryResponse;
import com.bizcore.sales.domain.model.OrderStatus;
import com.bizcore.sales.domain.port.in.ListOrdersUseCase;
import com.bizcore.sales.domain.port.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListOrdersUseCaseImpl implements ListOrdersUseCase {

    private final OrderRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> list(UUID tenantId, UUID branchId, UUID customerId, OrderStatus status, Pageable pageable) {
        return repository.findAll(tenantId, branchId, customerId, status, pageable)
                .map(OrderSummaryResponse::from);
    }
}
