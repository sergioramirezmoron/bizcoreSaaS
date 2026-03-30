package com.bizcore.sales.domain.port.in;

import com.bizcore.sales.application.dto.CustomerRequest;
import com.bizcore.sales.application.dto.CustomerResponse;

import java.util.UUID;

public interface CreateCustomerUseCase {
    CustomerResponse create(UUID tenantId, CustomerRequest request);
}
