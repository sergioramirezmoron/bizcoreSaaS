package com.bizcore.billing.infrastructure.stripe;

import com.bizcore.billing.domain.model.StripeEventData;
import com.bizcore.billing.domain.port.out.StripePort;
import com.bizcore.company.domain.model.SubscriptionPlan;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StripeAdapter implements StripePort {

    private final StripeProperties stripeProperties;

    @PostConstruct
    void init() {
        if (stripeProperties.getSecretKey() != null && !stripeProperties.getSecretKey().isBlank()) {
            Stripe.apiKey = stripeProperties.getSecretKey();
        }
    }

    @Override
    public String createCheckoutSession(UUID tenantId, SubscriptionPlan plan,
                                        String successUrl, String cancelUrl,
                                        String stripeCouponId) {
        try {
            String priceId = stripeProperties.priceIdForPlan(plan);

            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .putMetadata("tenantId", tenantId.toString())
                    .putMetadata("plan", plan.name())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(priceId)
                                    .setQuantity(1L)
                                    .build()
                    );

            if (stripeCouponId != null && !stripeCouponId.isBlank()) {
                builder.addDiscount(
                        SessionCreateParams.Discount.builder()
                                .setCoupon(stripeCouponId)
                                .build()
                );
            }

            Session session = Session.create(builder.build());
            return session.getUrl();

        } catch (StripeException e) {
            throw new RuntimeException("Error creando Stripe checkout session: " + e.getMessage(), e);
        }
    }

    @Override
    public StripeEventData parseWebhookEvent(byte[] rawBody, String stripeSignature) {
        String payload = new String(rawBody, StandardCharsets.UTF_8);
        Event event;
        try {
            event = Webhook.constructEvent(payload, stripeSignature,
                    stripeProperties.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            throw new SecurityException("Stripe webhook signature inválida", e);
        }

        return switch (event.getType()) {
            case "checkout.session.completed"    -> parseCheckoutSession(event);
            case "customer.subscription.updated",
                 "customer.subscription.deleted" -> parseSubscription(event);
            case "invoice.payment_failed"        -> parseInvoice(event);
            default -> new StripeEventData(event.getType(), null, null, null, null, null);
        };
    }

    // ─── parsers privados ────────────────────────────────────────────────────

    private StripeEventData parseCheckoutSession(Event event) {
        StripeObject obj = event.getDataObjectDeserializer().getObject().orElse(null);
        if (!(obj instanceof Session session)) {
            return unknown(event.getType());
        }

        UUID tenantId = null;
        String planName = null;
        if (session.getMetadata() != null) {
            String raw = session.getMetadata().get("tenantId");
            if (raw != null) tenantId = UUID.fromString(raw);
            planName = session.getMetadata().get("plan");
        }

        return new StripeEventData(event.getType(),
                session.getCustomer(), session.getSubscription(),
                null, tenantId, planName);
    }

    private StripeEventData parseSubscription(Event event) {
        StripeObject obj = event.getDataObjectDeserializer().getObject().orElse(null);
        if (!(obj instanceof Subscription sub)) {
            return unknown(event.getType());
        }

        String priceId = null;
        if (sub.getItems() != null
                && sub.getItems().getData() != null
                && !sub.getItems().getData().isEmpty()) {
            priceId = sub.getItems().getData().get(0).getPrice().getId();
        }

        // El tenantId se resuelve en el controller vía BillingStripeCustomerPort
        return new StripeEventData(event.getType(),
                sub.getCustomer(), sub.getId(),
                priceId, null, null);
    }

    private StripeEventData parseInvoice(Event event) {
        StripeObject obj = event.getDataObjectDeserializer().getObject().orElse(null);
        if (!(obj instanceof Invoice invoice)) {
            return unknown(event.getType());
        }
        return new StripeEventData(event.getType(),
                invoice.getCustomer(), invoice.getSubscription(),
                null, null, null);
    }

    @Override
    public String createBillingPortalSession(String stripeCustomerId, String returnUrl) {
        try {
            com.stripe.param.billingportal.SessionCreateParams params =
                    com.stripe.param.billingportal.SessionCreateParams.builder()
                            .setCustomer(stripeCustomerId)
                            .setReturnUrl(returnUrl)
                            .build();
            com.stripe.model.billingportal.Session session =
                    com.stripe.model.billingportal.Session.create(params);
            return session.getUrl();
        } catch (StripeException e) {
            throw new RuntimeException("Error creando Stripe billing portal session: " + e.getMessage(), e);
        }
    }

    private StripeEventData unknown(String type) {
        return new StripeEventData(type, null, null, null, null, null);
    }
}
