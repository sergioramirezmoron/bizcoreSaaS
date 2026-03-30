package com.bizcore.sales.infrastructure.persistence;

import com.bizcore.sales.domain.model.Customer;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class CustomerJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "total_spent", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSpent;

    @Column(name = "order_count", nullable = false)
    private int orderCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CustomerJpaEntity() {}

    public static CustomerJpaEntity from(Customer c) {
        CustomerJpaEntity e = new CustomerJpaEntity();
        e.id = c.id(); e.tenantId = c.tenantId(); e.firstName = c.firstName();
        e.lastName = c.lastName(); e.email = c.email(); e.phone = c.phone();
        e.notes = c.notes(); e.totalSpent = c.totalSpent(); e.orderCount = c.orderCount();
        e.createdAt = c.createdAt();
        return e;
    }

    public Customer toDomain() {
        return new Customer(id, tenantId, firstName, lastName, email, phone, notes,
                totalSpent, orderCount, createdAt);
    }
}
