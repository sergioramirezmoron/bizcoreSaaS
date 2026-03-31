package com.bizcore.suppliers.application.usecase;

import com.bizcore.suppliers.application.dto.SupplierResponse;
import com.bizcore.suppliers.domain.port.in.GetSupplierUseCase;
import com.bizcore.suppliers.domain.port.out.SupplierRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetSupplierUseCaseImpl implements GetSupplierUseCase {

    private final SupplierRepositoryPort repo;

    @Override
    public SupplierResponse get(UUID tenantId, UUID supplierId) {
        return repo.findByIdAndTenantId(supplierId, tenantId)
                .map(SupplierResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + supplierId));
    }
}
