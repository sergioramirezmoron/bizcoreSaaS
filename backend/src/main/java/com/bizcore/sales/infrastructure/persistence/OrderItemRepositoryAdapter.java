package com.bizcore.sales.infrastructure.persistence;

import com.bizcore.sales.domain.model.OrderItem;
import com.bizcore.sales.domain.port.out.OrderItemRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryAdapter implements OrderItemRepositoryPort {

    private final OrderItemJpaRepository jpaRepository;

    @Override
    public OrderItem save(OrderItem item) {
        return jpaRepository.save(OrderItemJpaEntity.from(item)).toDomain();
    }

    @Override
    public List<OrderItem> findAllByOrderId(UUID tenantId, UUID orderId) {
        return jpaRepository.findAllByOrderIdAndTenantId(orderId, tenantId).stream()
                .map(OrderItemJpaEntity::toDomain).toList();
    }

    @Override
    public void deleteAllByOrderId(UUID tenantId, UUID orderId) {
        jpaRepository.deleteAllByOrderIdAndTenantId(orderId, tenantId);
    }
}
