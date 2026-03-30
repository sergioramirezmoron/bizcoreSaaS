package com.bizcore.billing.domain.port.out;

import com.bizcore.billing.domain.model.StripeEventData;
import com.bizcore.company.domain.model.SubscriptionPlan;

import java.util.UUID;

public interface StripePort {
    /** Crea una Stripe Checkout Session y devuelve la URL de pago. */
    String createCheckoutSession(UUID tenantId, SubscriptionPlan plan,
                                 String successUrl, String cancelUrl,
                                 String stripeCouponId);

    /** Verifica la firma del webhook y devuelve un StripeEventData tipado. */
    StripeEventData parseWebhookEvent(byte[] rawBody, String stripeSignature);
}
