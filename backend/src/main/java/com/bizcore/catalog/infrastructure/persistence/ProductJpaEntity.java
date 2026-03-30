package com.bizcore.catalog.infrastructure.persistence;

import com.bizcore.catalog.domain.model.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "products")
public class ProductJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 100)
    private String sku;

    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "selling_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(length = 50)
    private String unit;

    @Convert(converter = JsonbConverter.class)
    @Column(name = "custom_fields", columnDefinition = "jsonb")
    private Map<String, Object> customFields;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProductJpaEntity() {}

    public static ProductJpaEntity from(Product p) {
        ProductJpaEntity e = new ProductJpaEntity();
        e.id = p.id();
        e.tenantId = p.tenantId();
        e.categoryId = p.categoryId();
        e.name = p.name();
        e.description = p.description();
        e.sku = p.sku();
        e.purchasePrice = p.purchasePrice();
        e.sellingPrice = p.sellingPrice();
        e.taxRate = p.taxRate();
        e.unit = p.unit();
        e.customFields = p.customFields();
        e.active = p.active();
        e.createdAt = p.createdAt();
        e.updatedAt = p.updatedAt();
        return e;
    }

    public Product toDomain() {
        return new Product(id, tenantId, categoryId, name, description, sku,
                purchasePrice, sellingPrice, taxRate, unit, customFields, active, createdAt, updatedAt);
    }
}
