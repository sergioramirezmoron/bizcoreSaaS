package com.bizcore.billing.application.usecase;

import com.bizcore.billing.domain.model.StripeEventData;
import com.bizcore.billing.domain.port.in.HandleStripeWebhookUseCase;
import com.bizcore.billing.domain.port.out.BillingCompanyPort;
import com.bizcore.billing.domain.port.out.BillingStripeCustomerPort;
import com.bizcore.billing.infrastructure.stripe.PlanLimits;
import com.bizcore.billing.infrastructure.stripe.StripeProperties;
import com.bizcore.company.domain.model.Company;
import com.bizcore.company.domain.model.SubscriptionPlan;
import com.bizcore.company.domain.model.SubscriptionStatus;
import com.bizcore.notifications.application.NotificationEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandleStripeWebhookUseCaseImpl implements HandleStripeWebhookUseCase {

    private final BillingCompanyPort billingCompanyPort;
    private final BillingStripeCustomerPort stripeCustomerPort;
    private final NotificationEventPublisher notificationPublisher;
    private final StripeProperties stripeProperties;

    @Override
    @Transactional
    public void handle(StripeEventData event) {
        switch (event.type()) {
            case "checkout.session.completed"      -> handleCheckoutCompleted(event);
            case "customer.subscription.updated"   -> handleSubscriptionUpdated(event);
            case "customer.subscription.deleted"   -> handleSubscriptionDeleted(event);
            case "invoice.payment_failed"          -> handlePaymentFailed(event);
            default -> log.debug("Stripe event ignorado: {}", event.type());
        }
    }

    /** Checkout completado: activa el tenant con el plan contratado. */
    private void handleCheckoutCompleted(StripeEventData event) {
        if (event.tenantId() == null || event.planName() == null) {
            log.warn("checkout.session.completed sin metadata tenantId/plan — ignorado");
            return;
        }

        Company company = billingCompanyPort.findById(event.tenantId()).orElse(null);
        if (company == null) {
            log.error("checkout.session.completed: tenant {} no encontrado", event.tenantId());
            return;
        }

        SubscriptionPlan plan = parsePlan(event.planName());
        Company activated = withSubscription(company, plan, SubscriptionStatus.ACTIVE,
                event.stripeCustomerId(), event.stripeSubscriptionId());

        billingCompanyPort.save(activated);
        stripeCustomerPort.save(event.stripeCustomerId(), event.tenantId());

        log.info("Tenant {} activado con plan {} (sub: {})",
                event.tenantId(), plan, event.stripeSubscriptionId());

        notificationPublisher.subscriptionActivated(event.tenantId(), activated.name(), plan);
    }

    /** Suscripción actualizada: actualiza plan y límites. */
    private void handleSubscriptionUpdated(StripeEventData event) {
        if (event.tenantId() == null) {
            log.warn("handleSubscriptionUpdated: tenantId no disponible en TenantContext");
            return;
        }
        Company company = billingCompanyPort.findById(event.tenantId()).orElse(null);
        if (company == null) return;

        SubscriptionPlan plan = event.stripePriceId() != null
                ? stripeProperties.planForPriceId(event.stripePriceId())
                : company.plan();

        // Si el plan era TRIAL o desconocido, cae a BASIC
        if (plan == SubscriptionPlan.TRIAL) {
            plan = company.plan() != SubscriptionPlan.TRIAL ? company.plan() : SubscriptionPlan.BASIC;
        }

        billingCompanyPort.save(withSubscription(company, plan, SubscriptionStatus.ACTIVE,
                event.stripeCustomerId(), event.stripeSubscriptionId()));

        log.info("Tenant {} plan actualizado a {}", event.tenantId(), plan);
    }

    /** Suscripción cancelada: degrada a TRIAL y marca como cancelada. */
    private void handleSubscriptionDeleted(StripeEventData event) {
        if (event.tenantId() == null) return;
        Company company = billingCompanyPort.findById(event.tenantId()).orElse(null);
        if (company == null) return;

        Company suspended = new Company(
                company.id(), company.name(), company.businessTypeId(),
                company.taxId(), company.phone(), company.address(),
                company.timezone(), company.logoUrl(), company.email(),
                SubscriptionPlan.TRIAL, SubscriptionStatus.CANCELLED, null,
                company.stripeCustomerId(), null,
                PlanLimits.maxEmployees(SubscriptionPlan.TRIAL),
                PlanLimits.maxBranches(SubscriptionPlan.TRIAL),
                PlanLimits.maxProducts(SubscriptionPlan.TRIAL),
                PlanLimits.maxProductImages(SubscriptionPlan.TRIAL),
                false, company.createdAt(), OffsetDateTime.now()
        );
        billingCompanyPort.save(suspended);
        notificationPublisher.subscriptionCancelled(event.tenantId(), company.name());
        log.info("Tenant {} suspendido (suscripción cancelada)", event.tenantId());
    }

    /** Pago fallido: marca como PAST_DUE y notifica. */
    private void handlePaymentFailed(StripeEventData event) {
        log.warn("Pago fallido para stripeCustomerId={}. Tenant: {}",
                event.stripeCustomerId(), event.tenantId());
        if (event.tenantId() == null) return;
        billingCompanyPort.findById(event.tenantId()).ifPresent(company -> {
            Company pastDue = new Company(
                    company.id(), company.name(), company.businessTypeId(),
                    company.taxId(), company.phone(), company.address(),
                    company.timezone(), company.logoUrl(), company.email(),
                    company.plan(), SubscriptionStatus.PAST_DUE, company.planExpiresAt(),
                    company.stripeCustomerId(), company.stripeSubscriptionId(),
                    company.maxEmployees(), company.maxBranches(),
                    company.maxProducts(), company.maxProductImages(),
                    company.active(), company.createdAt(), OffsetDateTime.now()
            );
            billingCompanyPort.save(pastDue);
            notificationPublisher.paymentFailed(event.tenantId(), company.name(), company.plan());
        });
    }

    // ─── helpers ────────────────────────────────────────────────────────────

    private Company withSubscription(Company c, SubscriptionPlan plan,
                                      SubscriptionStatus status,
                                      String customerId, String subscriptionId) {
        return new Company(
                c.id(), c.name(), c.businessTypeId(),
                c.taxId(), c.phone(), c.address(),
                c.timezone(), c.logoUrl(), c.email(),
                plan, status, null,
                customerId, subscriptionId,
                PlanLimits.maxEmployees(plan),
                PlanLimits.maxBranches(plan),
                PlanLimits.maxProducts(plan),
                PlanLimits.maxProductImages(plan),
                true, c.createdAt(), OffsetDateTime.now()
        );
    }

    private SubscriptionPlan parsePlan(String name) {
        try {
            return SubscriptionPlan.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Plan desconocido '{}', usando BASIC", name);
            return SubscriptionPlan.BASIC;
        }
    }

}
