package com.bizcore.sales.domain.port.in;

import com.bizcore.sales.application.dto.CustomerRequest;
import com.bizcore.sales.application.dto.CustomerResponse;

import java.util.UUID;

public interface UpdateCustomerUseCase {
    CustomerResponse update(UUID tenantId, UUID customerId, CustomerRequest request);
}
