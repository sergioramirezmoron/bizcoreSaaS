package com.bizcore.catalog.application.usecase;

import com.bizcore.catalog.domain.model.ProductImage;
import com.bizcore.catalog.domain.port.in.DeleteProductImageUseCase;
import com.bizcore.catalog.domain.port.out.ProductImageRepositoryPort;
import com.bizcore.catalog.domain.port.out.StoragePort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteProductImageUseCaseImpl implements DeleteProductImageUseCase {

    private final ProductImageRepositoryPort imageRepository;
    private final StoragePort storage;

    @Override
    @Transactional
    public void delete(UUID tenantId, UUID imageId) {
        ProductImage image = imageRepository.findById(tenantId, imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        storage.delete(image.r2Key());
        imageRepository.deleteById(tenantId, imageId);
    }
}
