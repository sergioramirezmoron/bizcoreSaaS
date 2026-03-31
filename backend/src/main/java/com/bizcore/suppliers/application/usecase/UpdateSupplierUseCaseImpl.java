package com.bizcore.suppliers.application.usecase;

import com.bizcore.suppliers.application.dto.SupplierRequest;
import com.bizcore.suppliers.application.dto.SupplierResponse;
import com.bizcore.suppliers.domain.model.Supplier;
import com.bizcore.suppliers.domain.port.in.UpdateSupplierUseCase;
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
public class UpdateSupplierUseCaseImpl implements UpdateSupplierUseCase {

    private final SupplierRepositoryPort repo;

    @Override
    public SupplierResponse update(UUID tenantId, UUID supplierId, SupplierRequest request) {
        Supplier existing = repo.findByIdAndTenantId(supplierId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + supplierId));

        Supplier updated = new Supplier(
                existing.id(),
                existing.tenantId(),
                request.name(),
                request.contactName(),
                request.email(),
                request.phone(),
                request.address(),
                request.website(),
                request.notes(),
                existing.active(),
                existing.createdAt(),
                Instant.now()
        );
        return SupplierResponse.from(repo.save(updated));
    }
}
