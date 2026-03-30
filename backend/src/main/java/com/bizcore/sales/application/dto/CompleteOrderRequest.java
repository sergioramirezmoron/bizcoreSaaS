package com.bizcore.sales.application.dto;

import com.bizcore.sales.domain.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record CompleteOrderRequest(
        @NotNull PaymentMethod paymentMethod
) {}
