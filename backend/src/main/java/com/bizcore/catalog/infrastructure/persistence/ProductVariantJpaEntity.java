package com.bizcore.catalog.infrastructure.persistence;

import com.bizcore.catalog.domain.model.ProductVariant;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
public class ProductVariantJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_name", length = 100)
    private String variantName;

    @Column(length = 100)
    private String sku;

    @Convert(converter = JsonbConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes;

    @Column(name = "selling_price", precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    protected ProductVariantJpaEntity() {}

    public static ProductVariantJpaEntity from(ProductVariant v) {
        ProductVariantJpaEntity e = new ProductVariantJpaEntity();
        e.id = v.id();
        e.tenantId = v.tenantId();
        e.productId = v.productId();
        e.variantName = v.variantName();
        e.sku = v.sku();
        e.attributes = v.attributes();
        e.sellingPrice = v.sellingPrice();
        e.active = v.active();
        return e;
    }

    public ProductVariant toDomain() {
        return new ProductVariant(id, tenantId, productId, variantName, sku, attributes, sellingPrice, active);
    }
}
