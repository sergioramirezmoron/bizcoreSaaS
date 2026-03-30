package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.application.dto.ProductVariantRequest;
import com.bizcore.catalog.application.dto.ProductVariantResponse;
import com.bizcore.catalog.domain.model.ProductVariant;
import com.bizcore.catalog.domain.port.in.UpdateProductVariantUseCase;
import com.bizcore.catalog.domain.port.out.ProductVariantRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateProductVariantUseCaseImpl implements UpdateProductVariantUseCase {

    private final ProductVariantRepositoryPort repository;

    @Override
    @Transactional
    public ProductVariantResponse update(UUID tenantId, UUID variantId, ProductVariantRequest request) {
        ProductVariant existing = repository.findById(tenantId, variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        ProductVariant updated = new ProductVariant(
                existing.id(), existing.tenantId(), existing.productId(),
                request.variantName(), request.sku(), request.attributes(),
                request.sellingPrice(), request.active()
        );
        return ProductVariantResponse.from(repository.save(updated));
    }
}
