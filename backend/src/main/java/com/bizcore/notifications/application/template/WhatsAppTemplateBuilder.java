package com.bizcore.notifications.application.template;

import com.bizcore.notifications.domain.model.NotificationType;

import java.util.Map;

/**
 * Construye el mensaje de texto para WhatsApp según el tipo de notificación.
 */
public final class WhatsAppTemplateBuilder {

    private WhatsAppTemplateBuilder() {}

    public static String message(NotificationType type, Map<String, String> p) {
        return switch (type) {
            case WELCOME -> "👋 Bienvenido a *BizCore*, %s. Tu empresa *%s* ya está configurada."
                    .formatted(p.getOrDefault("firstName", ""), p.getOrDefault("companyName", ""));

            case SUBSCRIPTION_ACTIVATED -> "✅ Plan *%s* activado para *%s*. ¡Ya tienes acceso a todas las funciones!"
                    .formatted(p.getOrDefault("plan", ""), p.getOrDefault("companyName", ""));

            case SUBSCRIPTION_CANCELLED -> "⚠️ La suscripción de *%s* ha sido cancelada. Visita bizcore.app/billing para reactivarla."
                    .formatted(p.getOrDefault("companyName", ""));

            case PAYMENT_FAILED -> "🔴 Pago fallido en *BizCore* (%s). Actualiza tu método de pago en bizcore.app/billing para evitar la suspensión."
                    .formatted(p.getOrDefault("companyName", ""));

            case LOW_STOCK -> "📦 Stock bajo: *%s* — solo %s unidades (mínimo: %s). Considera reponer pronto."
                    .formatted(p.getOrDefault("productName", ""),
                               p.getOrDefault("quantity", "0"),
                               p.getOrDefault("minQuantity", "0"));

            case ORDER_RECEIPT -> "🧾 Pedido *%s* completado. Total: *%s €*. ¡Gracias por tu compra en %s!"
                    .formatted(p.getOrDefault("orderNumber", ""),
                               p.getOrDefault("total", "0.00"),
                               p.getOrDefault("companyName", ""));

            case EMPLOYEE_INVITED -> "👤 Has sido invitado a unirte a *%s* como *%s* en BizCore. Acepta la invitación: %s"
                    .formatted(p.getOrDefault("companyName", ""),
                               p.getOrDefault("role", "empleado"),
                               p.getOrDefault("inviteUrl", "bizcore.app"));
        };
    }
}
