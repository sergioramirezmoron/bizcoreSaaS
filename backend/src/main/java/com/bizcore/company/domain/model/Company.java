package com.bizcore.company.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad de dominio: Empresa (tenant).
 * Sin dependencias de Spring ni JPA.
 */
public record Company(
        UUID id,
        String name,
        UUID businessTypeId,
        String taxId,
        String phone,
        String address,
        String timezone,
        String logoUrl,
        String email,
        SubscriptionPlan plan,
        SubscriptionStatus subscriptionStatus,
        OffsetDateTime planExpiresAt,
        String stripeCustomerId,
        String stripeSubscriptionId,
        int maxEmployees,
        int maxBranches,
        int maxProducts,
        int maxProductImages,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public boolean isTrialExpired() {
        return plan == SubscriptionPlan.TRIAL
                && planExpiresAt != null
                && OffsetDateTime.now().isAfter(planExpiresAt);
    }

    public boolean canAddEmployee(int currentCount) {
        return currentCount < maxEmployees;
    }

    public boolean canAddBranch(int currentCount) {
        return currentCount < maxBranches;
    }

    public boolean canAddProduct(int currentCount) {
        return currentCount < maxProducts;
    }
}
