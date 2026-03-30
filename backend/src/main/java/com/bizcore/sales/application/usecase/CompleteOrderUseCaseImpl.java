package com.bizcore.sales.application.usecase;

import com.bizcore.inventory.domain.model.MovementType;
import com.bizcore.inventory.domain.model.StockItem;
import com.bizcore.inventory.domain.model.StockMovement;
import com.bizcore.inventory.domain.port.out.StockItemRepositoryPort;
import com.bizcore.inventory.domain.port.out.StockMovementRepositoryPort;
import com.bizcore.sales.application.dto.CompleteOrderRequest;
import com.bizcore.sales.application.dto.OrderItemResponse;
import com.bizcore.sales.application.dto.OrderResponse;
import com.bizcore.sales.domain.model.Customer;
import com.bizcore.sales.domain.model.Order;
import com.bizcore.sales.domain.model.OrderItem;
import com.bizcore.sales.domain.model.OrderStatus;
import com.bizcore.sales.domain.port.in.CompleteOrderUseCase;
import com.bizcore.sales.domain.port.out.CustomerRepositoryPort;
import com.bizcore.sales.domain.port.out.OrderItemRepositoryPort;
import com.bizcore.sales.domain.port.out.OrderRepositoryPort;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompleteOrderUseCaseImpl implements CompleteOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderItemRepositoryPort orderItemRepository;
    private final CustomerRepositoryPort customerRepository;
    private final StockItemRepositoryPort stockItemRepository;
    private final StockMovementRepositoryPort stockMovementRepository;

    @Override
    @Transactional
    public OrderResponse complete(UUID tenantId, UUID orderId, CompleteOrderRequest request) {
        Order order = orderRepository.findById(tenantId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.status() != OrderStatus.PENDING) {
            throw new BusinessException("Only PENDING orders can be completed");
        }

        Instant now = Instant.now();
        Order completed = new Order(order.id(), order.tenantId(), order.branchId(), order.orderNumber(),
                order.employeeId(), order.customerId(), OrderStatus.COMPLETED,
                order.subtotal(), order.discountAmount(), order.taxAmount(), order.total(),
                request.paymentMethod(), order.notes(), now, order.createdAt(), now);
        Order saved = orderRepository.save(completed);

        List<OrderItem> items = orderItemRepository.findAllByOrderId(tenantId, orderId);

        // Decrementar stock por cada línea (silencioso si no hay stock item configurado)
        items.forEach(item -> decrementStock(tenantId, item, order.branchId(), orderId, order.orderNumber(), now));

        // Actualizar stats del cliente si aplica
        if (order.customerId() != null) {
            customerRepository.findById(tenantId, order.customerId()).ifPresent(customer -> {
                Customer updated = new Customer(customer.id(), customer.tenantId(),
                        customer.firstName(), customer.lastName(), customer.email(),
                        customer.phone(), customer.notes(),
                        customer.totalSpent().add(order.total()),
                        customer.orderCount() + 1, customer.createdAt());
                customerRepository.save(updated);
            });
        }

        return OrderResponse.from(saved, items.stream().map(OrderItemResponse::from).toList());
    }

    private void decrementStock(UUID tenantId, OrderItem item, UUID branchId,
                                UUID orderId, String orderNumber, Instant now) {
        if (item.productId() == null && item.variantId() == null) return;

        stockItemRepository.findByKey(tenantId, item.productId(), item.variantId(), branchId)
                .ifPresent(stockItem -> {
                    BigDecimal newQty = stockItem.quantity().subtract(item.quantity());
                    if (newQty.compareTo(BigDecimal.ZERO) < 0) return; // no forzar stock negativo

                    StockItem updated = new StockItem(stockItem.id(), stockItem.tenantId(),
                            stockItem.productId(), stockItem.variantId(), stockItem.branchId(),
                            newQty, stockItem.minQuantity(), stockItem.maxQuantity(),
                            stockItem.location(), now);
                    stockItemRepository.save(updated);

                    stockMovementRepository.save(new StockMovement(UUID.randomUUID(), tenantId,
                            stockItem.id(), MovementType.ORDER,
                            item.quantity().negate(), stockItem.quantity(), newQty,
                            orderId, "Pedido " + orderNumber, null, now));
                });
    }
}
