package com.bizcore.superadmin.domain.port.in;

import java.util.UUID;

public interface DeactivatePromoCodeUseCase {
    void deactivate(UUID promoCodeId);
}
