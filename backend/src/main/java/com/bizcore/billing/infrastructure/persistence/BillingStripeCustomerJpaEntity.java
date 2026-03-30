package com.bizcore.billing.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Tabla de lookup sin RLS: resuelve stripeCustomerId → tenantId
 * para webhooks donde no hay JWT/TenantContext disponible.
 */
@Entity
@Table(name = "billing_stripe_customers")
public class BillingStripeCustomerJpaEntity {

    @Id
    @Column(name = "stripe_customer_id", length = 100)
    private String stripeCustomerId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    protected BillingStripeCustomerJpaEntity() {}

    public BillingStripeCustomerJpaEntity(String stripeCustomerId, UUID tenantId) {
        this.stripeCustomerId = stripeCustomerId;
        this.tenantId = tenantId;
    }

    public String getStripeCustomerId() { return stripeCustomerId; }
    public UUID getTenantId()           { return tenantId; }
}
