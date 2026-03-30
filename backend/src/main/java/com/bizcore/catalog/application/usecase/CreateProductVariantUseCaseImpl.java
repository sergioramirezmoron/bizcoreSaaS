package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.application.dto.ProductVariantRequest;
import com.bizcore.catalog.application.dto.ProductVariantResponse;
import com.bizcore.catalog.domain.model.ProductVariant;
import com.bizcore.catalog.domain.port.in.CreateProductVariantUseCase;
import com.bizcore.catalog.domain.port.out.ProductRepositoryPort;
import com.bizcore.catalog.domain.port.out.ProductVariantRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateProductVariantUseCaseImpl implements CreateProductVariantUseCase {

    private final ProductRepositoryPort productRepository;
    private final ProductVariantRepositoryPort variantRepository;

    @Override
    @Transactional
    public ProductVariantResponse create(UUID tenantId, UUID productId, ProductVariantRequest request) {
        productRepository.findById(tenantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        ProductVariant variant = new ProductVariant(
                UUID.randomUUID(),
                tenantId,
                productId,
                request.variantName(),
                request.sku(),
                request.attributes(),
                request.sellingPrice(),
                request.active()
        );
        return ProductVariantResponse.from(variantRepository.save(variant));
    }
}
