package com.bizcore.company.domain.model;

/**
 * Estado de la suscripción de la empresa.
 * Independiente del plan (BASIC/STANDARD/PREMIUM) — describe la situación de pago.
 */
public enum SubscriptionStatus {
    /** Período de prueba activo (sin tarjeta). */
    TRIAL,
    /** Suscripción activa y al corriente de pago. */
    ACTIVE,
    /** Pago fallido; Stripe reintentará en los próximos días. */
    PAST_DUE,
    /** Suscripción cancelada. Acceso hasta fin del período pagado. */
    CANCELLED,
    /** Suspendida manualmente por el superadmin. */
    SUSPENDED
}
