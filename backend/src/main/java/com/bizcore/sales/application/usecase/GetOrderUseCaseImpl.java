package com.bizcore.sales.application.usecase;

import com.bizcore.sales.application.dto.OrderItemResponse;
import com.bizcore.sales.application.dto.OrderResponse;
import com.bizcore.sales.domain.port.in.GetOrderUseCase;
import com.bizcore.sales.domain.port.out.OrderItemRepositoryPort;
import com.bizcore.sales.domain.port.out.OrderRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetOrderUseCaseImpl implements GetOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderItemRepositoryPort orderItemRepository;

    @Override
    @Transactional(readOnly = true)
    public OrderResponse get(UUID tenantId, UUID orderId) {
        var order = orderRepository.findById(tenantId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        List<OrderItemResponse> items = orderItemRepository.findAllByOrderId(tenantId, orderId)
                .stream().map(OrderItemResponse::from).toList();
        return OrderResponse.from(order, items);
    }
}
