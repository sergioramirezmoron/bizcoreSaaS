package com.bizcore.billing.infrastructure.persistence;

import com.bizcore.billing.domain.model.PromoCode;
import com.bizcore.billing.domain.port.out.PromoCodeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PromoCodeRepositoryAdapter implements PromoCodeRepositoryPort {

    private final PromoCodeJpaRepository jpaRepository;

    @Override
    public Optional<PromoCode> findByCode(String code) {
        return jpaRepository.findByCode(code).map(PromoCodeJpaEntity::toDomain);
    }

    @Override
    public PromoCode save(PromoCode promoCode) {
        return jpaRepository.save(PromoCodeJpaEntity.from(promoCode)).toDomain();
    }
}
