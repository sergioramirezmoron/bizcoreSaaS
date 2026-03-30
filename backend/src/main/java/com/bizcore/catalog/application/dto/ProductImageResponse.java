package com.bizcore.catalog.application.dto;

import com.bizcore.catalog.domain.model.ProductImage;

import java.time.Instant;
import java.util.UUID;

public record ProductImageResponse(
        UUID id,
        String imageUrl,
        long sizeBytes,
        int sortOrder,
        Instant uploadedAt
) {
    public static ProductImageResponse from(ProductImage img) {
        return new ProductImageResponse(img.id(), img.imageUrl(), img.sizeBytes(), img.sortOrder(), img.uploadedAt());
    }
}
