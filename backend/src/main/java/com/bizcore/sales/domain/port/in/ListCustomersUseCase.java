package com.bizcore.sales.domain.port.in;

import com.bizcore.sales.application.dto.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListCustomersUseCase {
    Page<CustomerResponse> list(UUID tenantId, String search, Pageable pageable);
}
