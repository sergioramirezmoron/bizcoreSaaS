package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.application.dto.CreateProductRequest;
import com.bizcore.catalog.application.dto.ProductResponse;
import com.bizcore.catalog.domain.model.Product;
import com.bizcore.catalog.domain.port.in.CreateProductUseCase;
import com.bizcore.catalog.domain.port.out.ProductRepositoryPort;
import com.bizcore.shared.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateProductUseCaseImpl implements CreateProductUseCase {

    private final ProductRepositoryPort repository;

    @Override
    @Transactional
    public ProductResponse create(UUID tenantId, CreateProductRequest request) {
        if (request.sku() != null && repository.existsBySkuAndTenantId(request.sku(), tenantId)) {
            throw new ConflictException("SKU already exists");
        }
        Instant now = Instant.now();
        Product product = new Product(
                UUID.randomUUID(),
                tenantId,
                request.categoryId(),
                request.name(),
                request.description(),
                request.sku(),
                request.purchasePrice(),
                request.sellingPrice(),
                request.taxRate(),
                request.unit(),
                request.customFields(),
                true,
                now,
                now
        );
        return ProductResponse.from(repository.save(product), List.of(), List.of());
    }
}
