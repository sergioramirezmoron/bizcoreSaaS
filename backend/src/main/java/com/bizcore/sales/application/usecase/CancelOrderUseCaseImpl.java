package com.bizcore.sales.application.usecase;

import com.bizcore.sales.domain.model.Order;
import com.bizcore.sales.domain.model.OrderStatus;
import com.bizcore.sales.domain.port.in.CancelOrderUseCase;
import com.bizcore.sales.domain.port.out.OrderRepositoryPort;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelOrderUseCaseImpl implements CancelOrderUseCase {

    private final OrderRepositoryPort repository;

    @Override
    @Transactional
    public void cancel(UUID tenantId, UUID orderId) {
        Order order = repository.findById(tenantId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.status() != OrderStatus.PENDING) {
            throw new BusinessException("Only PENDING orders can be cancelled");
        }

        Instant now = Instant.now();
        Order cancelled = new Order(order.id(), order.tenantId(), order.branchId(), order.orderNumber(),
                order.employeeId(), order.customerId(), OrderStatus.CANCELLED,
                order.subtotal(), order.discountAmount(), order.taxAmount(), order.total(),
                order.paymentMethod(), order.notes(), null, order.createdAt(), now);
        repository.save(cancelled);
    }
}
