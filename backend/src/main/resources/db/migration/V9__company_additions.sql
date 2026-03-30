-- ============================================================
-- V9 — Company additions: subscription_status, email, audit tables
-- ============================================================

-- ── 1. Add missing columns to companies ────────────────────────────────────

ALTER TABLE companies
    ADD COLUMN IF NOT EXISTS email                VARCHAR(150),
    ADD COLUMN IF NOT EXISTS subscription_status  VARCHAR(20) NOT NULL DEFAULT 'TRIAL',
    ADD COLUMN IF NOT EXISTS currency             VARCHAR(10) NOT NULL DEFAULT 'EUR',
    ADD COLUMN IF NOT EXISTS locale               VARCHAR(10) NOT NULL DEFAULT 'es-ES',
    ADD COLUMN IF NOT EXISTS admin_notes          TEXT,
    ADD COLUMN IF NOT EXISTS plan_override_by_admin BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE companies
    ADD CONSTRAINT ck_companies_subscription_status
        CHECK (subscription_status IN ('TRIAL', 'ACTIVE', 'PAST_DUE', 'CANCELLED', 'SUSPENDED'));

-- ── 2. Add missing columns to promo_codes ───────────────────────────────────

ALTER TABLE promo_codes
    ADD COLUMN IF NOT EXISTS description      TEXT,
    ADD COLUMN IF NOT EXISTS type             VARCHAR(30) NOT NULL DEFAULT 'PERCENT_DISCOUNT',
    ADD COLUMN IF NOT EXISTS value            JSONB       NOT NULL DEFAULT '{}',
    ADD COLUMN IF NOT EXISTS applicable_plans TEXT[],
    ADD COLUMN IF NOT EXISTS valid_from       TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN IF NOT EXISTS valid_until      TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS is_active        BOOLEAN     NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS created_by       UUID        REFERENCES users(id);

ALTER TABLE promo_codes
    ADD CONSTRAINT ck_promo_codes_type
        CHECK (type IN ('TRIAL_EXTENSION', 'PERCENT_DISCOUNT', 'FREE_MONTHS', 'PLAN_UPGRADE'));

-- ── 3. promo_code_usages ────────────────────────────────────────────────────

CREATE TABLE promo_code_usages (
    id             UUID        NOT NULL DEFAULT gen_random_uuid(),
    promo_code_id  UUID        NOT NULL REFERENCES promo_codes(id),
    company_id     UUID        NOT NULL REFERENCES companies(id),
    applied_at     TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_promo_code_usages               PRIMARY KEY (id),
    CONSTRAINT uq_promo_code_usages_code_company   UNIQUE (promo_code_id, company_id)
);

CREATE INDEX idx_promo_code_usages_promo ON promo_code_usages (promo_code_id);
CREATE INDEX idx_promo_code_usages_company ON promo_code_usages (company_id);

-- Sin RLS: tabla de plataforma, gestionada por SUPER_ADMIN

-- ── 4. audit_logs ───────────────────────────────────────────────────────────

CREATE TABLE audit_logs (
    id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL,
    user_id     UUID         REFERENCES users(id),
    action      VARCHAR(50)  NOT NULL,   -- CREATE | UPDATE | DELETE | LOGIN | LOGOUT | etc.
    entity_type VARCHAR(50)  NOT NULL,
    entity_id   UUID,
    old_value   JSONB,
    new_value   JSONB,
    ip_address  VARCHAR(45),
    user_agent  TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_audit_logs PRIMARY KEY (id)
);

CREATE INDEX idx_audit_logs_tenant_date ON audit_logs (tenant_id, created_at DESC);
CREATE INDEX idx_audit_logs_entity      ON audit_logs (tenant_id, entity_type, entity_id);
CREATE INDEX idx_audit_logs_user        ON audit_logs (user_id, created_at DESC);

ALTER TABLE audit_logs ENABLE ROW LEVEL SECURITY;

CREATE POLICY audit_logs_tenant_isolation ON audit_logs
    USING (tenant_id::text = current_setting('app.current_tenant', true));

-- ── 5. admin_actions ────────────────────────────────────────────────────────

CREATE TABLE admin_actions (
    id           UUID        NOT NULL DEFAULT gen_random_uuid(),
    company_id   UUID        NOT NULL REFERENCES companies(id),
    action_type  VARCHAR(50) NOT NULL,
    -- FREE_MONTHS | PERCENT_DISCOUNT | PLAN_OVERRIDE | TRIAL_EXTEND | SUSPEND | REACTIVATE | NOTE | PROMO_CODE_MANUAL
    description  TEXT        NOT NULL,
    value        JSONB,
    performed_by UUID        NOT NULL REFERENCES users(id),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_admin_actions PRIMARY KEY (id)
);

CREATE INDEX idx_admin_actions_company ON admin_actions (company_id, created_at DESC);

-- Sin RLS: tabla de plataforma gestionada por SUPER_ADMIN

-- ── 6. alert_configs ────────────────────────────────────────────────────────

CREATE TABLE alert_configs (
    id              UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL,
    alert_type      VARCHAR(50)  NOT NULL,
    is_enabled      BOOLEAN      NOT NULL DEFAULT TRUE,
    threshold_value DECIMAL(10,2),
    notify_email    BOOLEAN      NOT NULL DEFAULT TRUE,
    notify_whatsapp BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_alert_configs PRIMARY KEY (id),
    CONSTRAINT uq_alert_configs_tenant_type UNIQUE (tenant_id, alert_type)
);

CREATE INDEX idx_alert_configs_tenant ON alert_configs (tenant_id);

ALTER TABLE alert_configs ENABLE ROW LEVEL SECURITY;

CREATE POLICY alert_configs_tenant_isolation ON alert_configs
    USING (tenant_id::text = current_setting('app.current_tenant', true));

-- ── 7. alert_notifications ──────────────────────────────────────────────────

CREATE TABLE alert_notifications (
    id           UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id    UUID         NOT NULL,
    alert_type   VARCHAR(50)  NOT NULL,
    title        VARCHAR(200) NOT NULL,
    message      TEXT         NOT NULL,
    reference_id UUID,
    is_read      BOOLEAN      NOT NULL DEFAULT FALSE,
    sent_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_alert_notifications PRIMARY KEY (id)
);

CREATE INDEX idx_alert_notifications_tenant      ON alert_notifications (tenant_id, sent_at DESC);
CREATE INDEX idx_alert_notifications_tenant_read ON alert_notifications (tenant_id, is_read)
    WHERE is_read = FALSE;

ALTER TABLE alert_notifications ENABLE ROW LEVEL SECURITY;

CREATE POLICY alert_notifications_tenant_isolation ON alert_notifications
    USING (tenant_id::text = current_setting('app.current_tenant', true));
