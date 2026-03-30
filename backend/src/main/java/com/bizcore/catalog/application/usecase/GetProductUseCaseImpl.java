package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.application.dto.ProductImageResponse;
import com.bizcore.catalog.application.dto.ProductResponse;
import com.bizcore.catalog.application.dto.ProductVariantResponse;
import com.bizcore.catalog.domain.port.in.GetProductUseCase;
import com.bizcore.catalog.domain.port.out.ProductImageRepositoryPort;
import com.bizcore.catalog.domain.port.out.ProductRepositoryPort;
import com.bizcore.catalog.domain.port.out.ProductVariantRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetProductUseCaseImpl implements GetProductUseCase {

    private final ProductRepositoryPort productRepository;
    private final ProductImageRepositoryPort imageRepository;
    private final ProductVariantRepositoryPort variantRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductResponse get(UUID tenantId, UUID productId) {
        var product = productRepository.findById(tenantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        List<ProductImageResponse> images = imageRepository.findAllByProductId(tenantId, productId)
                .stream().map(ProductImageResponse::from).toList();

        List<ProductVariantResponse> variants = variantRepository.findAllByProductId(tenantId, productId)
                .stream().map(ProductVariantResponse::from).toList();

        return ProductResponse.from(product, images, variants);
    }
}
