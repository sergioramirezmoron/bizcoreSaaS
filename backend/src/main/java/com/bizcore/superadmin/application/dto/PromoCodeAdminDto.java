package com.bizcore.superadmin.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PromoCodeAdminDto(
        UUID id,
        String code,
        String description,
        String type,
        String valueJson,
        Integer maxUses,
        int currentUses,
        Instant validFrom,
        Instant validUntil,
        boolean active,
        String stripeCouponId,
        UUID createdBy,
        Instant createdAt
) {}
