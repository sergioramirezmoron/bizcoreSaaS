package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.domain.port.in.DeleteProductVariantUseCase;
import com.bizcore.catalog.domain.port.out.ProductVariantRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteProductVariantUseCaseImpl implements DeleteProductVariantUseCase {

    private final ProductVariantRepositoryPort repository;

    @Override
    @Transactional
    public void delete(UUID tenantId, UUID variantId) {
        if (repository.findById(tenantId, variantId).isEmpty()) {
            throw new ResourceNotFoundException("Variant not found");
        }
        repository.deleteById(tenantId, variantId);
    }
}
