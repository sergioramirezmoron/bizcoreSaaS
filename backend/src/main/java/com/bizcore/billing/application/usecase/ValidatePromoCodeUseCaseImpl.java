package com.bizcore.billing.application.usecase;

import com.bizcore.billing.application.dto.PromoCodeResponse;
import com.bizcore.billing.domain.model.PromoCode;
import com.bizcore.billing.domain.model.PromoCodeStatus;
import com.bizcore.billing.domain.port.in.ValidatePromoCodeUseCase;
import com.bizcore.billing.domain.port.out.PromoCodeRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ValidatePromoCodeUseCaseImpl implements ValidatePromoCodeUseCase {

    private final PromoCodeRepositoryPort promoCodeRepository;

    @Override
    @Transactional(readOnly = true)
    public PromoCodeResponse validate(String code) {
        PromoCode promoCode = promoCodeRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Código promocional no encontrado"));

        boolean valid = promoCode.status() == PromoCodeStatus.ACTIVE
                && (promoCode.expiresAt() == null || !LocalDate.now().isAfter(promoCode.expiresAt()))
                && (promoCode.maxUses() == 0 || promoCode.currentUses() < promoCode.maxUses());

        return new PromoCodeResponse(promoCode.code(), promoCode.discountPercent(),
                promoCode.stripeCouponId(), valid);
    }
}
