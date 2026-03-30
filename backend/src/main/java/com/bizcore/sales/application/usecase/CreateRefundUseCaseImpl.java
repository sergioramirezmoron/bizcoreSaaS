package com.bizcore.sales.application.usecase;

import com.bizcore.inventory.domain.model.MovementType;
import com.bizcore.inventory.domain.model.StockItem;
import com.bizcore.inventory.domain.model.StockMovement;
import com.bizcore.inventory.domain.port.out.StockItemRepositoryPort;
import com.bizcore.inventory.domain.port.out.StockMovementRepositoryPort;
import com.bizcore.sales.application.dto.RefundRequest;
import com.bizcore.sales.application.dto.RefundResponse;
import com.bizcore.sales.domain.model.Order;
import com.bizcore.sales.domain.model.OrderStatus;
import com.bizcore.sales.domain.model.Refund;
import com.bizcore.sales.domain.port.in.CreateRefundUseCase;
import com.bizcore.sales.domain.port.out.OrderItemRepositoryPort;
import com.bizcore.sales.domain.port.out.OrderRepositoryPort;
import com.bizcore.sales.domain.port.out.RefundRepositoryPort;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateRefundUseCaseImpl implements CreateRefundUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderItemRepositoryPort orderItemRepository;
    private final RefundRepositoryPort refundRepository;
    private final StockItemRepositoryPort stockItemRepository;
    private final StockMovementRepositoryPort stockMovementRepository;

    @Override
    @Transactional
    public RefundResponse refund(UUID tenantId, UUID orderId, UUID employeeId, RefundRequest request) {
        Order order = orderRepository.findById(tenantId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.status() != OrderStatus.COMPLETED) {
            throw new BusinessException("Only COMPLETED orders can be refunded");
        }

        BigDecimal alreadyRefunded = refundRepository.sumRefundedAmount(tenantId, orderId);
        BigDecimal remaining = order.total().subtract(alreadyRefunded);
        if (request.refundAmount().compareTo(remaining) > 0) {
            throw new BusinessException(
                    "Refund amount %s exceeds remaining refundable amount %s".formatted(request.refundAmount(), remaining));
        }

        Instant now = Instant.now();
        Refund refund = new Refund(UUID.randomUUID(), tenantId, orderId, employeeId,
                request.refundAmount(), request.reason(), request.refundType(),
                request.refundMethod(), now);
        Refund saved = refundRepository.save(refund);

        boolean fullRefund = request.refundAmount().compareTo(remaining) == 0;
        if (fullRefund) {
            Order refunded = new Order(order.id(), order.tenantId(), order.branchId(), order.orderNumber(),
                    order.employeeId(), order.customerId(), OrderStatus.REFUNDED,
                    order.subtotal(), order.discountAmount(), order.taxAmount(), order.total(),
                    order.paymentMethod(), order.notes(), order.completedAt(), order.createdAt(), now);
            orderRepository.save(refunded);
        }

        // Restaurar stock si se solicitó
        if (request.restoreStock()) {
            orderItemRepository.findAllByOrderId(tenantId, orderId).forEach(item -> {
                if (item.productId() == null && item.variantId() == null) return;
                stockItemRepository.findByKey(tenantId, item.productId(), item.variantId(), order.branchId())
                        .ifPresent(stockItem -> {
                            BigDecimal newQty = stockItem.quantity().add(item.quantity());
                            StockItem updated = new StockItem(stockItem.id(), stockItem.tenantId(),
                                    stockItem.productId(), stockItem.variantId(), stockItem.branchId(),
                                    newQty, stockItem.minQuantity(), stockItem.maxQuantity(),
                                    stockItem.location(), now);
                            stockItemRepository.save(updated);
                            stockMovementRepository.save(new StockMovement(UUID.randomUUID(), tenantId,
                                    stockItem.id(), MovementType.RETURN,
                                    item.quantity(), stockItem.quantity(), newQty,
                                    orderId, "Devolución pedido " + order.orderNumber(), null, now));
                        });
            });
        }

        return RefundResponse.from(saved);
    }
}
