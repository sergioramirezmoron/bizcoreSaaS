package com.bizcore.suppliers.domain.port.in;

import com.bizcore.suppliers.application.dto.SupplierResponse;

import java.util.List;
import java.util.UUID;

public interface ListSuppliersUseCase {
    List<SupplierResponse> list(UUID tenantId);
}
