package com.bizcore.sales.domain.port.out;

import com.bizcore.sales.domain.model.OrderItem;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepositoryPort {
    OrderItem save(OrderItem item);
    List<OrderItem> findAllByOrderId(UUID tenantId, UUID orderId);
    void deleteAllByOrderId(UUID tenantId, UUID orderId);
}
