package com.bizcore.superadmin.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreatePromoCodeRequest(
        @NotBlank @Size(max = 50) String code,
        String description,
        @NotNull String type,           // TRIAL_EXTENSION | PERCENT_DISCOUNT | FREE_MONTHS | PLAN_UPGRADE
        @NotNull String valueJson,      // {"days":30} | {"percent":20,"duration_months":3} | {"months":2} | {"plan":"STANDARD"}
        Integer maxUses,                // null = ilimitado
        Instant validFrom,
        Instant validUntil,             // null = permanente
        String stripeCouponId
) {}
