package com.bizcore.suppliers.domain.port.in;

import com.bizcore.suppliers.application.dto.SupplierResponse;

import java.util.UUID;

public interface GetSupplierUseCase {
    SupplierResponse get(UUID tenantId, UUID supplierId);
}
