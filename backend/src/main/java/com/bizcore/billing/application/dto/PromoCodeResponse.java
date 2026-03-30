package com.bizcore.billing.application.dto;

public record PromoCodeResponse(
        String code,
        int discountPercent,
        String stripeCouponId,
        boolean valid
) {}
