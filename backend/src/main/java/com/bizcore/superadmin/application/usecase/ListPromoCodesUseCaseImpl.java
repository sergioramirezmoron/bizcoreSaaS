package com.bizcore.superadmin.application.usecase;

import com.bizcore.billing.infrastructure.persistence.PromoCodeJpaEntity;
import com.bizcore.shared.response.PageResponse;
import com.bizcore.superadmin.application.dto.PromoCodeAdminDto;
import com.bizcore.superadmin.domain.port.in.ListPromoCodesUseCase;
import com.bizcore.superadmin.infrastructure.persistence.AdminPromoCodeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListPromoCodesUseCaseImpl implements ListPromoCodesUseCase {

    private final AdminPromoCodeJpaRepository promoRepo;

    @Override
    public PageResponse<PromoCodeAdminDto> list(Pageable pageable) {
        return PageResponse.from(promoRepo.findAll(pageable).map(this::toDto));
    }

    private PromoCodeAdminDto toDto(PromoCodeJpaEntity e) {
        return new PromoCodeAdminDto(
                e.getId(),
                e.getCode(),
                e.getDescription(),
                e.getType(),
                e.getValue(),
                e.getMaxUses() == 0 ? null : e.getMaxUses(),
                e.getCurrentUses(),
                e.getValidFrom(),
                e.getValidUntil(),
                e.isActive(),
                e.getStripeCouponId(),
                e.getCreatedBy(),
                e.getCreatedAt()
        );
    }
}
