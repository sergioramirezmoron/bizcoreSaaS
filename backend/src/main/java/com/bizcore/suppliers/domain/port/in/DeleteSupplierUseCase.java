package com.bizcore.suppliers.domain.port.in;

import java.util.UUID;

public interface DeleteSupplierUseCase {
    void delete(UUID tenantId, UUID supplierId);
}
