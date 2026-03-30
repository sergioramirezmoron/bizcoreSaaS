package com.bizcore.sales.domain.port.in;

import com.bizcore.sales.application.dto.OrderResponse;

import java.util.UUID;

public interface GetOrderUseCase {
    OrderResponse get(UUID tenantId, UUID orderId);
}
