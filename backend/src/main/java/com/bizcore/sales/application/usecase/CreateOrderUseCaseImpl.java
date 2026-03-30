package com.bizcore.sales.application.usecase;

import com.bizcore.sales.application.dto.CreateOrderRequest;
import com.bizcore.sales.application.dto.OrderItemResponse;
import com.bizcore.sales.application.dto.OrderResponse;
import com.bizcore.sales.domain.model.Order;
import com.bizcore.sales.domain.model.OrderItem;
import com.bizcore.sales.domain.model.OrderStatus;
import com.bizcore.sales.domain.port.in.CreateOrderUseCase;
import com.bizcore.sales.domain.port.out.OrderItemRepositoryPort;
import com.bizcore.sales.domain.port.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderItemRepositoryPort orderItemRepository;

    @Override
    @Transactional
    public OrderResponse create(UUID tenantId, UUID employeeId, CreateOrderRequest request) {
        List<OrderItem> items = request.items().stream().map(itemReq -> {
            BigDecimal taxRate       = itemReq.taxRate()         != null ? itemReq.taxRate()         : BigDecimal.ZERO;
            BigDecimal discountPct   = itemReq.discountPercent() != null ? itemReq.discountPercent() : BigDecimal.ZERO;

            BigDecimal itemSubtotal    = itemReq.quantity().multiply(itemReq.unitPrice());
            BigDecimal itemDiscount    = itemSubtotal.multiply(discountPct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal afterDiscount   = itemSubtotal.subtract(itemDiscount);
            BigDecimal itemTax         = afterDiscount.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal lineTotal       = afterDiscount.add(itemTax);

            return new OrderItem(UUID.randomUUID(), null, tenantId,
                    itemReq.productId(), itemReq.variantId(), itemReq.productName(),
                    itemReq.quantity(), itemReq.unitPrice(), taxRate, discountPct, lineTotal);
        }).toList();

        BigDecimal subtotal        = items.stream().map(i -> i.quantity().multiply(i.unitPrice())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discountAmount  = items.stream().map(i -> i.quantity().multiply(i.unitPrice()).multiply(i.discountPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxAmount       = items.stream().map(i -> i.lineTotal().subtract(i.quantity().multiply(i.unitPrice()).subtract(i.quantity().multiply(i.unitPrice()).multiply(i.discountPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)))).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total           = items.stream().map(OrderItem::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        int year = LocalDate.now().getYear();
        long seq  = orderRepository.countByTenantIdAndYear(tenantId, year) + 1;
        String orderNumber = "ORD-%d-%05d".formatted(year, seq);

        Instant now = Instant.now();
        Order order = new Order(UUID.randomUUID(), tenantId, request.branchId(), orderNumber,
                employeeId, request.customerId(), OrderStatus.PENDING,
                subtotal.setScale(2, RoundingMode.HALF_UP),
                discountAmount,
                taxAmount.setScale(2, RoundingMode.HALF_UP),
                total.setScale(2, RoundingMode.HALF_UP),
                null, request.notes(), null, now, now);

        Order saved = orderRepository.save(order);

        List<OrderItem> savedItems = items.stream()
                .map(i -> orderItemRepository.save(new OrderItem(i.id(), saved.id(), i.tenantId(),
                        i.productId(), i.variantId(), i.productName(),
                        i.quantity(), i.unitPrice(), i.taxRate(), i.discountPercent(), i.lineTotal())))
                .toList();

        return OrderResponse.from(saved, savedItems.stream().map(OrderItemResponse::from).toList());
    }
}
