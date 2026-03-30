package com.bizcore.company.infrastructure.persistence;

import com.bizcore.company.domain.model.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "business_type_id")
    private UUID businessTypeId;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false, length = 50)
    private String timezone;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "subscription_plan")
    private SubscriptionPlan plan;

    @Column(name = "plan_expires_at")
    private OffsetDateTime planExpiresAt;

    @Column(name = "stripe_customer_id", unique = true, length = 100)
    private String stripeCustomerId;

    @Column(name = "stripe_subscription_id", unique = true, length = 100)
    private String stripeSubscriptionId;

    @Column(name = "max_employees", nullable = false)
    private int maxEmployees;

    @Column(name = "max_branches", nullable = false)
    private int maxBranches;

    @Column(name = "max_products", nullable = false)
    private int maxProducts;

    @Column(name = "max_product_images", nullable = false)
    private int maxProductImages;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
