package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.domain.port.in.DeleteProductUseCase;
import com.bizcore.catalog.domain.port.out.ProductRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteProductUseCaseImpl implements DeleteProductUseCase {

    private final ProductRepositoryPort repository;

    @Override
    @Transactional
    public void delete(UUID tenantId, UUID productId) {
        if (repository.findById(tenantId, productId).isEmpty()) {
            throw new ResourceNotFoundException("Product not found");
        }
        repository.deleteById(tenantId, productId);
    }
}
