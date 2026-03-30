package com.bizcore.sales.infrastructure.persistence;

import com.bizcore.sales.domain.model.Order;
import com.bizcore.sales.domain.model.OrderStatus;
import com.bizcore.sales.domain.port.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Order save(Order order) {
        return jpaRepository.save(OrderJpaEntity.from(order)).toDomain();
    }

    @Override
    public Optional<Order> findById(UUID tenantId, UUID orderId) {
        return jpaRepository.findByIdAndTenantId(orderId, tenantId).map(OrderJpaEntity::toDomain);
    }

    @Override
    public Page<Order> findAll(UUID tenantId, UUID branchId, UUID customerId, OrderStatus status, Pageable pageable) {
        return jpaRepository.search(tenantId, branchId, customerId, status, pageable)
                .map(OrderJpaEntity::toDomain);
    }

    @Override
    public long countByTenantIdAndYear(UUID tenantId, int year) {
        return jpaRepository.countByTenantIdAndYear(tenantId, year);
    }
}
