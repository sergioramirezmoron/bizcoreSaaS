package com.bizcore.billing.domain.port.out;

import java.util.Optional;
import java.util.UUID;

/**
 * Tabla de lookup (sin RLS) para resolver stripeCustomerId → tenantId
 * en webhooks donde el tenantId no está disponible en el contexto del request.
 */
public interface BillingStripeCustomerPort {
    void save(String stripeCustomerId, UUID tenantId);
    Optional<UUID> findTenantIdByStripeCustomerId(String stripeCustomerId);
}
