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

    // ── V9 additions ──────────────────────────────────────────────────────────

    @Column(name = "value", columnDefinition = "jsonb")
    private String value;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 30)
    private String type;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "valid_from")
    private Instant validFrom;

    @Column(name = "valid_until")
    private Instant validUntil;

    @Column(name = "created_by")
    private UUID createdBy;

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
        e.description = p.description();
        e.type = p.type();
        e.isActive = p.active();
        e.validFrom = p.validFrom();
        e.validUntil = p.validUntil();
        e.createdBy = p.createdBy();
        e.createdAt = p.createdAt();
        e.value = "{}"; // default empty JSON; admin create flow sets it explicitly
        return e;
    }

    public PromoCode toDomain() {
        return new PromoCode(id, code, stripeCouponId, discountPercent, maxUses, currentUses,
                status, expiresAt, description, type, isActive, validFrom, validUntil, createdBy, createdAt);
    }

    // Getters for admin queries
    public UUID getId()           { return id; }
    public String getCode()       { return code; }
    public String getDescription(){ return description; }
    public String getType()       { return type; }
    public boolean isActive()     { return isActive; }
    public Instant getValidFrom() { return validFrom; }
    public Instant getValidUntil(){ return validUntil; }
    public UUID getCreatedBy()    { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public int getMaxUses()       { return maxUses; }
    public int getCurrentUses()   { return currentUses; }
    public String getStripeCouponId() { return stripeCouponId; }
    public int getDiscountPercent()   { return discountPercent; }
    public PromoCodeStatus getStatus(){ return status; }
    public LocalDate getExpiresAt()   { return expiresAt; }
    public String getValue()           { return value; }
    public void setValue(String v)     { this.value = v; }
}
