package com.bizcore.billing.infrastructure.stripe;

import com.bizcore.company.domain.model.SubscriptionPlan;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bizcore.stripe")
@Getter
@Setter
public class StripeProperties {

    private String secretKey;
    private String webhookSecret;
    private PriceId priceId = new PriceId();

    @Getter
    @Setter
    public static class PriceId {
        private String basic;
        private String standard;
        private String premium;
    }

    /** Resuelve el plan a partir de un Stripe price ID. */
    public SubscriptionPlan planForPriceId(String id) {
        if (id == null) return SubscriptionPlan.TRIAL;
        if (id.equals(priceId.basic))    return SubscriptionPlan.BASIC;
        if (id.equals(priceId.standard)) return SubscriptionPlan.STANDARD;
        if (id.equals(priceId.premium))  return SubscriptionPlan.PREMIUM;
        return SubscriptionPlan.TRIAL;
    }

    /** Devuelve el price ID de Stripe para un plan dado. */
    public String priceIdForPlan(SubscriptionPlan plan) {
        return switch (plan) {
            case BASIC    -> priceId.basic;
            case STANDARD -> priceId.standard;
            case PREMIUM  -> priceId.premium;
            default       -> throw new IllegalArgumentException("Plan sin price ID: " + plan);
        };
    }
}
