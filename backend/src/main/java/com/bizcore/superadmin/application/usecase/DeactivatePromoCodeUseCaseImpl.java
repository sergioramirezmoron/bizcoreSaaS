package com.bizcore.superadmin.application.usecase;

import com.bizcore.shared.exception.ResourceNotFoundException;
import com.bizcore.superadmin.domain.port.in.DeactivatePromoCodeUseCase;
import com.bizcore.superadmin.infrastructure.persistence.AdminPromoCodeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeactivatePromoCodeUseCaseImpl implements DeactivatePromoCodeUseCase {

    private final AdminPromoCodeJpaRepository promoRepo;

    @Override
    public void deactivate(UUID promoCodeId) {
        if (!promoRepo.existsById(promoCodeId)) {
            throw new ResourceNotFoundException("PromoCode not found: " + promoCodeId);
        }
        promoRepo.deactivate(promoCodeId);
    }
}
