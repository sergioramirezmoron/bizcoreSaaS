package com.bizcore.suppliers.application.usecase;

import com.bizcore.suppliers.application.dto.SupplierRequest;
import com.bizcore.suppliers.application.dto.SupplierResponse;
import com.bizcore.suppliers.domain.model.Supplier;
import com.bizcore.suppliers.domain.port.in.CreateSupplierUseCase;
import com.bizcore.suppliers.domain.port.out.SupplierRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateSupplierUseCaseImpl implements CreateSupplierUseCase {

    private final SupplierRepositoryPort repo;

    @Override
    public SupplierResponse create(UUID tenantId, SupplierRequest request) {
        Instant now = Instant.now();
        Supplier supplier = new Supplier(
                UUID.randomUUID(),
                tenantId,
                request.name(),
                request.contactName(),
                request.email(),
                request.phone(),
                request.address(),
                request.website(),
                request.notes(),
                true,
                now,
                now
        );
        return SupplierResponse.from(repo.save(supplier));
    }
}
