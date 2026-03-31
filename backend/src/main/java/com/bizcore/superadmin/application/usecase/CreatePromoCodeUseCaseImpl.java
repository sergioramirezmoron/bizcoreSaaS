package com.bizcore.superadmin.application.usecase;

import com.bizcore.billing.domain.model.PromoCodeStatus;
import com.bizcore.billing.infrastructure.persistence.PromoCodeJpaEntity;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.superadmin.application.dto.CreatePromoCodeRequest;
import com.bizcore.superadmin.application.dto.PromoCodeAdminDto;
import com.bizcore.superadmin.domain.port.in.CreatePromoCodeUseCase;
import com.bizcore.superadmin.infrastructure.persistence.AdminPromoCodeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreatePromoCodeUseCaseImpl implements CreatePromoCodeUseCase {

    private final AdminPromoCodeJpaRepository promoRepo;

    @Override
    public PromoCodeAdminDto create(UUID createdBy, CreatePromoCodeRequest request) {
        if (promoRepo.findByCode(request.code().toUpperCase()).isPresent()) {
            throw new BusinessException("Promo code already exists: " + request.code());
        }

        PromoCodeJpaEntity entity = buildEntity(request, createdBy);

        PromoCodeJpaEntity saved = promoRepo.save(entity);
        return toDto(saved);
    }

    private PromoCodeJpaEntity buildEntity(CreatePromoCodeRequest req, UUID createdBy) {
        // Build via a PromoCode domain object to reuse the existing from() method
        com.bizcore.billing.domain.model.PromoCode domain = new com.bizcore.billing.domain.model.PromoCode(
                UUID.randomUUID(),
                req.code().toUpperCase(),
                req.stripeCouponId(),
                0,               // discountPercent — flexible codes use valueJson
                req.maxUses() != null ? req.maxUses() : 0,
                0,
                PromoCodeStatus.ACTIVE,
                null,            // expiresAt (LocalDate) — not used for admin codes
                req.description(),
                req.type(),
                true,
                req.validFrom(),
                req.validUntil(),
                createdBy,
                Instant.now()
        );
        PromoCodeJpaEntity entity = PromoCodeJpaEntity.from(domain);
        entity.setValue(req.valueJson());
        return entity;
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
