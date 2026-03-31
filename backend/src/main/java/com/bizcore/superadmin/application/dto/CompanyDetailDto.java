package com.bizcore.superadmin.application.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CompanyDetailDto(
        UUID id,
        String name,
        String email,
        String taxId,
        String phone,
        String address,
        String timezone,
        String plan,
        String subscriptionStatus,
        OffsetDateTime planExpiresAt,
        String stripeCustomerId,
        String stripeSubscriptionId,
        String adminNotes,
        boolean planOverrideByAdmin,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        int maxEmployees,
        int maxBranches,
        int maxProducts,
        List<AdminActionDto> recentActions
) {}
