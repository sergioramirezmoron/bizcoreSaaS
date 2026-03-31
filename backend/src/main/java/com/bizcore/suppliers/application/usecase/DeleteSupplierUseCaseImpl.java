package com.bizcore.suppliers.application.usecase;

import com.bizcore.suppliers.domain.model.Supplier;
import com.bizcore.suppliers.domain.port.in.DeleteSupplierUseCase;
import com.bizcore.suppliers.domain.port.out.SupplierRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteSupplierUseCaseImpl implements DeleteSupplierUseCase {

    private final SupplierRepositoryPort repo;

    @Override
    public void delete(UUID tenantId, UUID supplierId) {
        Supplier existing = repo.findByIdAndTenantId(supplierId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + supplierId));

        // Borrado lógico
        Supplier deactivated = new Supplier(
                existing.id(), existing.tenantId(), existing.name(), existing.contactName(),
                existing.email(), existing.phone(), existing.address(), existing.website(),
                existing.notes(), false, existing.createdAt(), Instant.now()
        );
        repo.save(deactivated);
    }
}
