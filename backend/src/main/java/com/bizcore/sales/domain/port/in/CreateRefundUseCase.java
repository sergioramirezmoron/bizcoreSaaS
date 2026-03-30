package com.bizcore.sales.domain.port.in;

import com.bizcore.sales.application.dto.RefundRequest;
import com.bizcore.sales.application.dto.RefundResponse;

import java.util.UUID;

public interface CreateRefundUseCase {
    RefundResponse refund(UUID tenantId, UUID orderId, UUID employeeId, RefundRequest request);
}
