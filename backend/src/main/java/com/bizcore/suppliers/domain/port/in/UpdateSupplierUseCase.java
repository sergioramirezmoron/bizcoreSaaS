package com.bizcore.suppliers.domain.port.in;

import com.bizcore.suppliers.application.dto.SupplierRequest;
import com.bizcore.suppliers.application.dto.SupplierResponse;

import java.util.UUID;

public interface UpdateSupplierUseCase {
    SupplierResponse update(UUID tenantId, UUID supplierId, SupplierRequest request);
}
