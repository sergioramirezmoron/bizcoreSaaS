package com.bizcore.billing.domain.model;

import java.util.UUID;

/**
 * DTO de dominio que encapsula los datos relevantes de un evento Stripe.
 * Mantiene el SDK de Stripe confinado en la capa de infraestructura.
 */
public record StripeEventData(
        String type,
        String stripeCustomerId,
        String stripeSubscriptionId,
        String stripePriceId,
        UUID tenantId,    // solo presente en checkout.session.completed (metadata)
        String planName   // solo presente en checkout.session.completed (metadata)
) {}
