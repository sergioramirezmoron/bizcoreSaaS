package com.bizcore.sales.domain.port.in;

import com.bizcore.sales.application.dto.CompleteOrderRequest;
import com.bizcore.sales.application.dto.OrderResponse;

import java.util.UUID;

public interface CompleteOrderUseCase {
    OrderResponse complete(UUID tenantId, UUID orderId, CompleteOrderRequest request);
}
