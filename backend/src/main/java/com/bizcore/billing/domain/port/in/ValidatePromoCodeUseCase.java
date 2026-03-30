package com.bizcore.billing.domain.port.in;

import com.bizcore.billing.application.dto.PromoCodeResponse;

public interface ValidatePromoCodeUseCase {
    PromoCodeResponse validate(String code);
}
