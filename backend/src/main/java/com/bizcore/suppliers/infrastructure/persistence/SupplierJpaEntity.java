package com.bizcore.suppliers.infrastructure.persistence;

import com.bizcore.suppliers.domain.model.Supplier;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "suppliers")
public class SupplierJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "text")
    private String address;

    @Column(length = 255)
    private String website;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SupplierJpaEntity() {}

    public static SupplierJpaEntity from(Supplier s) {
        SupplierJpaEntity e = new SupplierJpaEntity();
        e.id = s.id();
        e.tenantId = s.tenantId();
        e.name = s.name();
        e.contactName = s.contactName();
        e.email = s.email();
        e.phone = s.phone();
        e.address = s.address();
        e.website = s.website();
        e.notes = s.notes();
        e.active = s.active();
        e.createdAt = s.createdAt();
        e.updatedAt = s.updatedAt();
        return e;
    }

    public Supplier toDomain() {
        return new Supplier(id, tenantId, name, contactName, email, phone,
                address, website, notes, active, createdAt, updatedAt);
    }
}
