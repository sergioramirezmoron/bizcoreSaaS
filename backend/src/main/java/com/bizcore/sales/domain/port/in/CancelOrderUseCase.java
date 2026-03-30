package com.bizcore.sales.domain.port.in;

import java.util.UUID;

public interface CancelOrderUseCase {
    void cancel(UUID tenantId, UUID orderId);
}
