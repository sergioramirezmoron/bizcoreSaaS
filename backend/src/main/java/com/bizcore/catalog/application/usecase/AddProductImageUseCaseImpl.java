package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.application.dto.ProductImageResponse;
import com.bizcore.catalog.domain.model.ProductImage;
import com.bizcore.catalog.domain.port.in.AddProductImageUseCase;
import com.bizcore.catalog.domain.port.out.ProductImageRepositoryPort;
import com.bizcore.catalog.domain.port.out.ProductRepositoryPort;
import com.bizcore.catalog.domain.port.out.StoragePort;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddProductImageUseCaseImpl implements AddProductImageUseCase {

    private static final int MAX_IMAGES_PER_PRODUCT = 10;

    private final ProductRepositoryPort productRepository;
    private final ProductImageRepositoryPort imageRepository;
    private final StoragePort storage;

    @Override
    @Transactional
    public ProductImageResponse add(UUID tenantId, UUID productId, String filename,
                                    String contentType, long sizeBytes, InputStream content) {
        productRepository.findById(tenantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        int count = imageRepository.countByProductId(tenantId, productId);
        if (count >= MAX_IMAGES_PER_PRODUCT) {
            throw new BusinessException("Maximum " + MAX_IMAGES_PER_PRODUCT + " images per product");
        }

        String key = "tenants/%s/products/%s/%s-%s".formatted(tenantId, productId, UUID.randomUUID(), filename);
        String url = storage.upload(key, contentType, sizeBytes, content);

        ProductImage image = new ProductImage(
                UUID.randomUUID(),
                tenantId,
                productId,
                key,
                url,
                sizeBytes,
                count,
                Instant.now()
        );
        return ProductImageResponse.from(imageRepository.save(image));
    }
}
