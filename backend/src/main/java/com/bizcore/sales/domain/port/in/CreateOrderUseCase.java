package com.bizcore.sales.domain.port.in;

import com.bizcore.sales.application.dto.CreateOrderRequest;
import com.bizcore.sales.application.dto.OrderResponse;

import java.util.UUID;

public interface CreateOrderUseCase {
    OrderResponse create(UUID tenantId, UUID employeeId, CreateOrderRequest request);
}
