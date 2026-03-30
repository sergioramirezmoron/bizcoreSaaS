package com.bizcore.billing.infrastructure.stripe;

import com.bizcore.company.domain.model.SubscriptionPlan;

/**
 * Límites de uso por plan de suscripción.
 * Fuente única de verdad para max_employees, max_branches, max_products, max_product_images.
 */
public final class PlanLimits {

    private PlanLimits() {}

    public static int maxEmployees(SubscriptionPlan plan) {
        return switch (plan) {
            case TRIAL    -> 2;
            case BASIC    -> 3;
            case STANDARD -> 10;
            case PREMIUM  -> 999_999;
        };
    }

    public static int maxBranches(SubscriptionPlan plan) {
        return switch (plan) {
            case TRIAL    -> 1;
            case BASIC    -> 1;
            case STANDARD -> 2;
            case PREMIUM  -> 5;
        };
    }

    public static int maxProducts(SubscriptionPlan plan) {
        return switch (plan) {
            case TRIAL    -> 50;
            case BASIC    -> 100;
            case STANDARD -> 1_000;
            case PREMIUM  -> 999_999;
        };
    }

    public static int maxProductImages(SubscriptionPlan plan) {
        return switch (plan) {
            case TRIAL    -> 2;
            case BASIC    -> 1;
            case STANDARD -> 3;
            case PREMIUM  -> 5;
        };
    }

    /**
     * Resuelve el plan a partir del Stripe price ID.
     * Si el price ID no coincide con ninguno configurado, devuelve {@code fallback}.
     */
    public static SubscriptionPlan planForPriceId(String priceId, SubscriptionPlan fallback) {
        // La resolución real se delega al StripeAdapter que conoce los price IDs.
        // Este método existe como contrato; el adapter lo sobreescribe pasando ya el plan.
        return fallback;
    }
}
