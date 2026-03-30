package com.bizcore.billing.domain.port.in;

import com.bizcore.billing.application.dto.CheckoutRequest;
import com.bizcore.billing.application.dto.CheckoutResponse;

import java.util.UUID;

public interface CreateCheckoutSessionUseCase {
    CheckoutResponse createSession(UUID tenantId, CheckoutRequest request);
}
