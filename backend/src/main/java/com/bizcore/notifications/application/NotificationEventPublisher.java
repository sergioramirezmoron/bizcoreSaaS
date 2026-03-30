package com.bizcore.notifications.application;

import com.bizcore.billing.infrastructure.stripe.PlanLimits;
import com.bizcore.company.domain.model.SubscriptionPlan;
import com.bizcore.notifications.application.dto.NotificationRequest;
import com.bizcore.notifications.domain.model.NotificationChannel;
import com.bizcore.notifications.domain.model.NotificationType;
import com.bizcore.notifications.domain.port.in.SendNotificationUseCase;
import com.bizcore.shared.domain.UserRole;
import com.bizcore.auth.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Fachada de notificaciones para otros módulos.
 * Encapsula la construcción de NotificationRequest y la resolución de destinatarios.
 * Los módulos externos (billing, inventory, sales) llaman a estos métodos directamente.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventPublisher {

    private final SendNotificationUseCase sendNotificationUseCase;
    private final UserJpaRepository userJpaRepository;

    // ─── Billing ────────────────────────────────────────────────────────────

    public void paymentFailed(UUID tenantId, String companyName, SubscriptionPlan plan) {
        findOwnerEmail(tenantId).ifPresent(email ->
                sendNotificationUseCase.send(new NotificationRequest(
                        tenantId, NotificationType.PAYMENT_FAILED,
                        Set.of(NotificationChannel.EMAIL),
                        email, null,
                        Map.of("companyName", companyName, "plan", plan.name())
                ))
        );
    }

    public void subscriptionActivated(UUID tenantId, String companyName, SubscriptionPlan plan) {
        findOwnerEmail(tenantId).ifPresent(email -> sendSubscriptionActivated(tenantId, companyName, plan, email));
    }

    public void subscriptionActivated(UUID tenantId, String companyName,
                                      SubscriptionPlan plan, String ownerEmail) {
        String email = (ownerEmail != null) ? ownerEmail
                : findOwnerEmail(tenantId).orElse(null);
        if (email != null) sendSubscriptionActivated(tenantId, companyName, plan, email);
    }

    private void sendSubscriptionActivated(UUID tenantId, String companyName,
                                           SubscriptionPlan plan, String email) {
        sendNotificationUseCase.send(new NotificationRequest(
                tenantId, NotificationType.SUBSCRIPTION_ACTIVATED,
                Set.of(NotificationChannel.EMAIL),
                email, null,
                Map.of(
                        "companyName",   companyName,
                        "plan",          plan.name(),
                        "maxEmployees",  String.valueOf(PlanLimits.maxEmployees(plan)),
                        "maxBranches",   String.valueOf(PlanLimits.maxBranches(plan)),
                        "maxProducts",   String.valueOf(PlanLimits.maxProducts(plan))
                )
        ));
    }

    public void subscriptionCancelled(UUID tenantId, String companyName) {
        findOwnerEmail(tenantId).ifPresent(email ->
                sendNotificationUseCase.send(new NotificationRequest(
                        tenantId, NotificationType.SUBSCRIPTION_CANCELLED,
                        Set.of(NotificationChannel.EMAIL),
                        email, null,
                        Map.of("companyName", companyName)
                ))
        );
    }

    // ─── Company / Onboarding ────────────────────────────────────────────────

    public void welcome(UUID tenantId, String firstName, String companyName, String email) {
        sendNotificationUseCase.send(new NotificationRequest(
                tenantId, NotificationType.WELCOME,
                Set.of(NotificationChannel.EMAIL),
                email, null,
                Map.of("firstName", firstName, "companyName", companyName)
        ));
    }

    public void employeeInvited(UUID tenantId, String recipientEmail,
                                String companyName, String role, String inviteUrl) {
        sendNotificationUseCase.send(new NotificationRequest(
                tenantId, NotificationType.EMPLOYEE_INVITED,
                Set.of(NotificationChannel.EMAIL),
                recipientEmail, null,
                Map.of("companyName", companyName, "role", role, "inviteUrl", inviteUrl)
        ));
    }

    // ─── Inventory ──────────────────────────────────────────────────────────

    public void lowStock(UUID tenantId, String productName,
                         BigDecimal quantity, BigDecimal minQuantity) {
        findOwnerEmail(tenantId).ifPresent(email ->
                sendNotificationUseCase.send(new NotificationRequest(
                        tenantId, NotificationType.LOW_STOCK,
                        Set.of(NotificationChannel.EMAIL),
                        email, null,
                        Map.of(
                                "productName", productName,
                                "quantity",    quantity.toPlainString(),
                                "minQuantity", minQuantity.toPlainString()
                        )
                ))
        );
    }

    // ─── Sales ──────────────────────────────────────────────────────────────

    public void orderReceipt(UUID tenantId, String customerEmail,
                             String orderNumber, String total,
                             String paymentMethod, String companyName) {
        if (customerEmail == null || customerEmail.isBlank()) return;
        sendNotificationUseCase.send(new NotificationRequest(
                tenantId, NotificationType.ORDER_RECEIPT,
                Set.of(NotificationChannel.EMAIL),
                customerEmail, null,
                Map.of(
                        "orderNumber",   orderNumber,
                        "total",         total,
                        "paymentMethod", paymentMethod,
                        "companyName",   companyName
                )
        ));
    }

    // ─── helper ─────────────────────────────────────────────────────────────

    private java.util.Optional<String> findOwnerEmail(UUID tenantId) {
        return userJpaRepository
                .findFirstByTenantIdAndRole(tenantId, UserRole.OWNER)
                .map(u -> u.getEmail());
    }
}
