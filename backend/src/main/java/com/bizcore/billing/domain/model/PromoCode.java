package com.bizcore.billing.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PromoCode(
        UUID id,
        String code,
        String stripeCouponId,
        int discountPercent,
        int maxUses,
        int currentUses,
        PromoCodeStatus status,
        LocalDate expiresAt,
        Instant createdAt
) {}
