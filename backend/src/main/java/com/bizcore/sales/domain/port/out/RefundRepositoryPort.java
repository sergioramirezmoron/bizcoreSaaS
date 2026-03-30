package com.bizcore.sales.domain.port.out;

import com.bizcore.sales.domain.model.Refund;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface RefundRepositoryPort {
    Refund save(Refund refund);
    List<Refund> findAllByOrderId(UUID tenantId, UUID orderId);
    BigDecimal sumRefundedAmount(UUID tenantId, UUID orderId);
}
