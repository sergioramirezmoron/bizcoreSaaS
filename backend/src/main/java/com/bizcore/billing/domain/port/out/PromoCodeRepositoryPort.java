package com.bizcore.billing.domain.port.out;

import com.bizcore.billing.domain.model.PromoCode;

import java.util.Optional;

public interface PromoCodeRepositoryPort {
    Optional<PromoCode> findByCode(String code);
    PromoCode save(PromoCode promoCode);
}
