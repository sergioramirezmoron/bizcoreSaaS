package com.bizcore.sales.domain.port.in;

import com.bizcore.sales.application.dto.OrderSummaryResponse;
import com.bizcore.sales.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListOrdersUseCase {
    Page<OrderSummaryResponse> list(UUID tenantId, UUID branchId, UUID customerId, OrderStatus status, Pageable pageable);
}
