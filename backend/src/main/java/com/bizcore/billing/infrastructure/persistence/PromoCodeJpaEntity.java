package com.bizcore.billing.infrastructure.persistence;

import com.bizcore.billing.domain.model.PromoCode;
import com.bizcore.billing.domain.model.PromoCodeStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "promo_codes")
public class PromoCodeJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "stripe_coupon_id", length = 100)
    private String stripeCouponId;

    @Column(name = "discount_percent", nullable = false)
    private int discountPercent;

    @Column(name = "max_uses", nullable = false)
    private int maxUses;

    @Column(name = "current_uses", nullable = false)
    private int currentUses;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PromoCodeStatus status;

    @Column(name = "expires_at")
    private LocalDate expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PromoCodeJpaEntity() {}

    public static PromoCodeJpaEntity from(PromoCode p) {
        PromoCodeJpaEntity e = new PromoCodeJpaEntity();
        e.id = p.id();
        e.code = p.code();
        e.stripeCouponId = p.stripeCouponId();
        e.discountPercent = p.discountPercent();
        e.maxUses = p.maxUses();
        e.currentUses = p.currentUses();
        e.status = p.status();
        e.expiresAt = p.expiresAt();
        e.createdAt = p.createdAt();
        return e;
    }

    public PromoCode toDomain() {
        return new PromoCode(id, code, stripeCouponId, discountPercent,
                maxUses, currentUses, status, expiresAt, createdAt);
    }
}
