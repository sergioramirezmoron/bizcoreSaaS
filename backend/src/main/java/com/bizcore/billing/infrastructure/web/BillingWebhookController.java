package com.bizcore.billing.infrastructure.web;

import com.bizcore.billing.domain.model.StripeEventData;
import com.bizcore.billing.domain.port.in.HandleStripeWebhookUseCase;
import com.bizcore.billing.domain.port.out.BillingStripeCustomerPort;
import com.bizcore.billing.domain.port.out.StripePort;
import com.bizcore.billing.infrastructure.stripe.StripeProperties;
import com.bizcore.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Endpoint público (sin JWT) para recibir eventos de Stripe.
 * La autenticación se realiza verificando la firma del header Stripe-Signature.
 *
 * IMPORTANTE: consume el body como byte[] para que Stripe pueda verificar la firma
 * sobre el payload raw sin modificar.
 */
@RestController
@RequestMapping("/api/v1/billing/webhooks")
@RequiredArgsConstructor
@Slf4j
public class BillingWebhookController {

    private final StripePort stripePort;
    private final HandleStripeWebhookUseCase handleStripeWebhookUseCase;
    private final BillingStripeCustomerPort stripeCustomerPort;

    @PostMapping(value = "/stripe", consumes = "application/json")
    public ResponseEntity<Void> stripe(
            @RequestBody byte[] payload,
            @RequestHeader("Stripe-Signature") String signature
    ) {
        StripeEventData event;
        try {
            event = stripePort.parseWebhookEvent(payload, signature);
        } catch (SecurityException e) {
            log.warn("Stripe webhook rechazado: firma inválida");
            return ResponseEntity.status(400).build();
        }

        // Resolver tenantId para eventos de suscripción (sin metadata de checkout)
        StripeEventData enriched = enrichWithTenant(event);

        try {
            TenantContext.setTenantId(enriched.tenantId());
            handleStripeWebhookUseCase.handle(enriched);
        } finally {
            TenantContext.clear();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Para eventos que no llevan tenantId en metadata (subscription.updated/deleted,
     * invoice.payment_failed), lo resuelve a través de la tabla de lookup sin RLS.
     */
    private StripeEventData enrichWithTenant(StripeEventData event) {
        if (event.tenantId() != null) return event;
        if (event.stripeCustomerId() == null) return event;

        UUID tenantId = stripeCustomerPort
                .findTenantIdByStripeCustomerId(event.stripeCustomerId())
                .orElse(null);

        if (tenantId == null) {
            log.warn("No se encontró tenant para stripeCustomerId={}", event.stripeCustomerId());
            return event;
        }

        return new StripeEventData(event.type(), event.stripeCustomerId(),
                event.stripeSubscriptionId(), event.stripePriceId(),
                tenantId, event.planName());
    }
}
