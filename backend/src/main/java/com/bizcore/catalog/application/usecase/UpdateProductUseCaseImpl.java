package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.application.dto.ProductImageResponse;
import com.bizcore.catalog.application.dto.ProductResponse;
import com.bizcore.catalog.application.dto.ProductVariantResponse;
import com.bizcore.catalog.application.dto.UpdateProductRequest;
import com.bizcore.catalog.domain.model.Product;
import com.bizcore.catalog.domain.port.in.UpdateProductUseCase;
import com.bizcore.catalog.domain.port.out.ProductImageRepositoryPort;
import com.bizcore.catalog.domain.port.out.ProductRepositoryPort;
import com.bizcore.catalog.domain.port.out.ProductVariantRepositoryPort;
import com.bizcore.shared.exception.ConflictException;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateProductUseCaseImpl implements UpdateProductUseCase {

    private final ProductRepositoryPort productRepository;
    private final ProductImageRepositoryPort imageRepository;
    private final ProductVariantRepositoryPort variantRepository;

    @Override
    @Transactional
    public ProductResponse update(UUID tenantId, UUID productId, UpdateProductRequest request) {
        Product existing = productRepository.findById(tenantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (request.sku() != null
                && !request.sku().equals(existing.sku())
                && productRepository.existsBySkuAndTenantId(request.sku(), tenantId)) {
            throw new ConflictException("SKU already exists");
        }

        Product updated = new Product(
                existing.id(), existing.tenantId(),
                request.categoryId(), request.name(), request.description(), request.sku(),
                request.purchasePrice(), request.sellingPrice(), request.taxRate(), request.unit(),
                request.customFields(), request.active(),
                existing.createdAt(), Instant.now()
        );

        List<ProductImageResponse> images = imageRepository.findAllByProductId(tenantId, productId)
                .stream().map(ProductImageResponse::from).toList();
        List<ProductVariantResponse> variants = variantRepository.findAllByProductId(tenantId, productId)
                .stream().map(ProductVariantResponse::from).toList();

        return ProductResponse.from(productRepository.save(updated), images, variants);
    }
}
