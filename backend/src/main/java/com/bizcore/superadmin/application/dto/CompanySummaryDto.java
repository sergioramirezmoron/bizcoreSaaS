package com.bizcore.superadmin.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CompanySummaryDto(
        UUID id,
        String name,
        String email,
        String plan,
        String subscriptionStatus,
        boolean active,
        OffsetDateTime planExpiresAt,
        OffsetDateTime createdAt
) {}
