package com.bizcore.superadmin.domain.port.in;

import com.bizcore.superadmin.application.dto.CreatePromoCodeRequest;
import com.bizcore.superadmin.application.dto.PromoCodeAdminDto;

import java.util.UUID;

public interface CreatePromoCodeUseCase {
    PromoCodeAdminDto create(UUID createdBy, CreatePromoCodeRequest request);
}
