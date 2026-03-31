package com.bizcore.suppliers.application.usecase;

import com.bizcore.suppliers.application.dto.SupplierResponse;
import com.bizcore.suppliers.domain.port.in.ListSuppliersUseCase;
import com.bizcore.suppliers.domain.port.out.SupplierRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListSuppliersUseCaseImpl implements ListSuppliersUseCase {

    private final SupplierRepositoryPort repo;

    @Override
    public List<SupplierResponse> list(UUID tenantId) {
        return repo.findAllByTenantId(tenantId)
                .stream()
                .map(SupplierResponse::from)
                .toList();
    }
}
