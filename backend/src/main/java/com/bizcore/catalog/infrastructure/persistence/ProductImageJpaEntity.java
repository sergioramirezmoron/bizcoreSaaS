package com.bizcore.catalog.infrastructure.persistence;

import com.bizcore.catalog.domain.model.ProductImage;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product_images")
public class ProductImageJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "r2_key", nullable = false, unique = true)
    private String r2Key;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "size_bytes", nullable = false)
    private int sizeBytes;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Instant uploadedAt;

    protected ProductImageJpaEntity() {}

    public static ProductImageJpaEntity from(ProductImage img) {
        ProductImageJpaEntity e = new ProductImageJpaEntity();
        e.id = img.id();
        e.tenantId = img.tenantId();
        e.productId = img.productId();
        e.r2Key = img.r2Key();
        e.imageUrl = img.imageUrl();
        e.sizeBytes = (int) img.sizeBytes();
        e.sortOrder = img.sortOrder();
        e.uploadedAt = img.uploadedAt();
        return e;
    }

    public ProductImage toDomain() {
        return new ProductImage(id, tenantId, productId, r2Key, imageUrl, sizeBytes, sortOrder, uploadedAt);
    }
}
