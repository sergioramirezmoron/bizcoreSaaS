package com.bizcore.billing.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "promo_code_usages")
public class PromoCodeUsageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "promo_code_id", nullable = false)
    private UUID promoCodeId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private Instant appliedAt;

    protected PromoCodeUsageJpaEntity() {}

    public static PromoCodeUsageJpaEntity of(UUID promoCodeId, UUID companyId) {
        PromoCodeUsageJpaEntity e = new PromoCodeUsageJpaEntity();
        e.promoCodeId = promoCodeId;
        e.companyId   = companyId;
        e.appliedAt   = Instant.now();
        return e;
    }

    public UUID getId()           { return id; }
    public UUID getPromoCodeId()  { return promoCodeId; }
    public UUID getCompanyId()    { return companyId; }
    public Instant getAppliedAt() { return appliedAt; }
}
