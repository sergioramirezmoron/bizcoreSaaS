package com.bizcore.superadmin.domain.port.in;

import com.bizcore.shared.response.PageResponse;
import com.bizcore.superadmin.application.dto.PromoCodeAdminDto;
import org.springframework.data.domain.Pageable;

public interface ListPromoCodesUseCase {
    PageResponse<PromoCodeAdminDto> list(Pageable pageable);
}
