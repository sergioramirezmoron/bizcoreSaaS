package com.bizcore.billing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PromoCodeUsageJpaRepository extends JpaRepository<PromoCodeUsageJpaEntity, UUID> {

    boolean existsByPromoCodeIdAndCompanyId(UUID promoCodeId, UUID companyId);

    long countByPromoCodeId(UUID promoCodeId);
}
