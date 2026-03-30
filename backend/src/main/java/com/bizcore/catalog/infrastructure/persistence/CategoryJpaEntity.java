package com.bizcore.catalog.infrastructure.persistence;

import com.bizcore.catalog.domain.model.Category;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categories")
public class CategoryJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(length = 7)
    private String color;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CategoryJpaEntity() {}

    public static CategoryJpaEntity from(Category c) {
        CategoryJpaEntity e = new CategoryJpaEntity();
        e.id = c.id();
        e.tenantId = c.tenantId();
        e.name = c.name();
        e.parentId = c.parentId();
        e.color = c.color();
        e.sortOrder = c.sortOrder();
        e.active = c.active();
        e.createdAt = c.createdAt();
        return e;
    }

    public Category toDomain() {
        return new Category(id, tenantId, name, parentId, color, sortOrder, active, createdAt);
    }
}
