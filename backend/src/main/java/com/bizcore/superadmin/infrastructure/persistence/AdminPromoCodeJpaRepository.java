package com.bizcore.superadmin.infrastructure.persistence;

import com.bizcore.billing.infrastructure.persistence.PromoCodeJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AdminPromoCodeJpaRepository extends JpaRepository<PromoCodeJpaEntity, UUID> {

    Page<PromoCodeJpaEntity> findAll(Pageable pageable);

    Optional<PromoCodeJpaEntity> findByCode(String code);

    @Modifying
    @Query(value = "UPDATE promo_codes SET is_active = false WHERE id = :id", nativeQuery = true)
    void deactivate(@Param("id") UUID id);
}
