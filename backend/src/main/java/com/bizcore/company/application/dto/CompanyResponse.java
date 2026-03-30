package com.bizcore.company.application.dto;

import com.bizcore.company.domain.model.SubscriptionPlan;
import com.bizcore.company.domain.model.SubscriptionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CompanyResponse(
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
        int maxEmployees,
        int maxBranches,
        int maxProducts,
        int maxProductImages,
        boolean active,
        OffsetDateTime createdAt
) {}
