package com.bizcore.sales.domain.port.in;

import com.bizcore.sales.application.dto.CustomerResponse;

import java.util.UUID;

public interface GetCustomerUseCase {
    CustomerResponse get(UUID tenantId, UUID customerId);
}
