package com.bizcore.sales.domain.port.out;

import com.bizcore.sales.domain.model.Order;
import com.bizcore.sales.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(UUID tenantId, UUID orderId);
    Page<Order> findAll(UUID tenantId, UUID branchId, UUID customerId, OrderStatus status, Pageable pageable);
    long countByTenantIdAndYear(UUID tenantId, int year);
}
