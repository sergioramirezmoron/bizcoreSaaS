package com.bizcore.notifications.application.template;

import com.bizcore.notifications.domain.model.NotificationType;

import java.util.Map;

/**
 * Construye el asunto y el HTML de cada tipo de notificación por email.
 */
public final class EmailTemplateBuilder {

    private EmailTemplateBuilder() {}

    public static String subject(NotificationType type, Map<String, String> p) {
        return switch (type) {
            case WELCOME                 -> "Bienvenido a BizCore 🎉";
            case SUBSCRIPTION_ACTIVATED  -> "Tu plan " + p.getOrDefault("plan", "") + " está activo";
            case SUBSCRIPTION_CANCELLED  -> "Tu suscripción ha sido cancelada";
            case PAYMENT_FAILED          -> "Acción requerida: pago fallido en BizCore";
            case LOW_STOCK               -> "⚠️ Stock bajo: " + p.getOrDefault("productName", "producto");
            case ORDER_RECEIPT           -> "Recibo pedido " + p.getOrDefault("orderNumber", "");
            case EMPLOYEE_INVITED        -> "Has sido invitado a unirte a " + p.getOrDefault("companyName", "BizCore");
        };
    }

    public static String html(NotificationType type, Map<String, String> p) {
        String body = switch (type) {
            case WELCOME -> """
                    <h2>¡Bienvenido a BizCore, %s!</h2>
                    <p>Tu empresa <strong>%s</strong> ya está configurada.</p>
                    <p>Empieza añadiendo tus productos y empleados desde el panel de control.</p>
                    """.formatted(
                    p.getOrDefault("firstName", ""),
                    p.getOrDefault("companyName", ""));

            case SUBSCRIPTION_ACTIVATED -> """
                    <h2>¡Plan activado! 🚀</h2>
                    <p>Tu empresa <strong>%s</strong> ahora tiene activo el plan <strong>%s</strong>.</p>
                    <ul>
                      <li>Empleados: hasta %s</li>
                      <li>Sucursales: hasta %s</li>
                      <li>Productos: hasta %s</li>
                    </ul>
                    """.formatted(
                    p.getOrDefault("companyName", ""),
                    p.getOrDefault("plan", ""),
                    p.getOrDefault("maxEmployees", "-"),
                    p.getOrDefault("maxBranches", "-"),
                    p.getOrDefault("maxProducts", "-"));

            case SUBSCRIPTION_CANCELLED -> """
                    <h2>Suscripción cancelada</h2>
                    <p>La suscripción de <strong>%s</strong> ha sido cancelada.</p>
                    <p>Tu cuenta ha pasado al plan gratuito con funcionalidades limitadas.</p>
                    <p>Si esto fue un error, <a href="https://bizcore.app/billing">reactiva tu plan aquí</a>.</p>
                    """.formatted(p.getOrDefault("companyName", ""));

            case PAYMENT_FAILED -> """
                    <h2>⚠️ Pago fallido</h2>
                    <p>No hemos podido procesar el pago de tu suscripción <strong>%s</strong> en <strong>%s</strong>.</p>
                    <p>Por favor, <a href="https://bizcore.app/billing">actualiza tu método de pago</a> para evitar la suspensión del servicio.</p>
                    """.formatted(
                    p.getOrDefault("plan", ""),
                    p.getOrDefault("companyName", ""));

            case LOW_STOCK -> """
                    <h2>Stock bajo: %s</h2>
                    <p>El producto <strong>%s</strong> tiene <strong>%s unidades</strong> restantes (mínimo: %s).</p>
                    <p>Considera realizar un pedido de reposición cuanto antes.</p>
                    """.formatted(
                    p.getOrDefault("productName", ""),
                    p.getOrDefault("productName", ""),
                    p.getOrDefault("quantity", "0"),
                    p.getOrDefault("minQuantity", "0"));

            case ORDER_RECEIPT -> """
                    <h2>Recibo del pedido %s</h2>
                    <p>Gracias por tu compra en <strong>%s</strong>.</p>
                    <p>Total: <strong>%s €</strong></p>
                    <p>Método de pago: %s</p>
                    """.formatted(
                    p.getOrDefault("orderNumber", ""),
                    p.getOrDefault("companyName", ""),
                    p.getOrDefault("total", "0.00"),
                    p.getOrDefault("paymentMethod", "-"));

            case EMPLOYEE_INVITED -> """
                    <h2>Invitación a %s</h2>
                    <p>Has sido invitado a unirte como <strong>%s</strong>.</p>
                    <p><a href="%s">Acepta la invitación aquí</a></p>
                    """.formatted(
                    p.getOrDefault("companyName", ""),
                    p.getOrDefault("role", "empleado"),
                    p.getOrDefault("inviteUrl", "#"));
        };

        return wrap(body);
    }

    private static String wrap(String content) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head><meta charset="UTF-8"></head>
                <body style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px;color:#333">
                  <div style="border-bottom:3px solid #4F46E5;margin-bottom:24px;padding-bottom:12px">
                    <span style="font-size:20px;font-weight:bold;color:#4F46E5">BizCore</span>
                  </div>
                  %s
                  <hr style="margin-top:32px;border:none;border-top:1px solid #eee">
                  <p style="font-size:12px;color:#999">
                    © BizCore — Plataforma de gestión para PYMEs<br>
                    Si no esperabas este correo, puedes ignorarlo.
                  </p>
                </body>
                </html>
                """.formatted(content);
    }
}
