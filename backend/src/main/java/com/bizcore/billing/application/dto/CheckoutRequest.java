package com.bizcore.billing.application.dto;

import com.bizcore.company.domain.model.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull SubscriptionPlan plan,
        String promoCode,
        @NotNull String successUrl,
        @NotNull String cancelUrl
) {}
