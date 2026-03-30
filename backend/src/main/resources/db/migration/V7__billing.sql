-- ============================================================
-- V7 — Billing: promo_codes + billing_stripe_customers
-- ============================================================

-- Códigos promocionales (globales, sin tenant — gestionados por SUPER_ADMIN)
CREATE TABLE promo_codes (
    id               UUID        NOT NULL DEFAULT gen_random_uuid(),
    code             VARCHAR(50) NOT NULL,
    stripe_coupon_id VARCHAR(100),
    discount_percent INT         NOT NULL DEFAULT 0,
    max_uses         INT         NOT NULL DEFAULT 0,   -- 0 = ilimitado
    current_uses     INT         NOT NULL DEFAULT 0,
    status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    expires_at       DATE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_promo_codes      PRIMARY KEY (id),
    CONSTRAINT uq_promo_codes_code UNIQUE (code),
    CONSTRAINT ck_promo_codes_status CHECK (status IN ('ACTIVE', 'EXPIRED', 'EXHAUSTED')),
    CONSTRAINT ck_promo_codes_discount CHECK (discount_percent BETWEEN 0 AND 100)
);

-- Sin RLS: los promo codes son globales y se validan sin contexto de tenant

-- ============================================================

-- Lookup table: stripeCustomerId → tenantId (sin RLS — necesario para webhooks)
-- Stripe no envía JWT; necesitamos resolver el tenant por su Stripe customer ID.
CREATE TABLE billing_stripe_customers (
    stripe_customer_id  VARCHAR(100) NOT NULL,
    tenant_id           UUID         NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_billing_stripe_customers PRIMARY KEY (stripe_customer_id)
);

CREATE INDEX idx_billing_stripe_customers_tenant ON billing_stripe_customers (tenant_id);

-- Sin RLS: tabla de administración interna para resolución de identidad
