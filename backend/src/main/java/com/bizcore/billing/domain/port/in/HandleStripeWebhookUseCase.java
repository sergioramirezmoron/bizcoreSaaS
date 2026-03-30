package com.bizcore.billing.domain.port.in;

import com.bizcore.billing.domain.model.StripeEventData;

public interface HandleStripeWebhookUseCase {
    void handle(StripeEventData event);
}
