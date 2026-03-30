-- ============================================================
-- V2 — Company: business_types + companies + branches
-- ============================================================

CREATE TYPE subscription_plan AS ENUM ('TRIAL', 'BASIC', 'STANDARD', 'PREMIUM');

-- ============================================================
-- business_types (catálogo global, sin tenant)
-- ============================================================
CREATE TABLE business_types (
    id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    code        VARCHAR(50)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    icon        VARCHAR(50),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_business_types      PRIMARY KEY (id),
    CONSTRAINT uq_business_types_code UNIQUE (code)
);

-- Seed inicial de tipos de negocio
INSERT INTO business_types (code, name, description, icon, is_active) VALUES
    ('CLOTHING_STORE',   'Tienda de ropa',      'Venta de ropa y accesorios',       'shirt',       TRUE),
    ('BAKERY',           'Panadería',            'Panadería y pastelería',            'cake',        TRUE),
    ('HARDWARE_STORE',   'Ferretería',           'Materiales y herramientas',         'wrench',      TRUE),
    ('GROCERY',          'Alimentación',         'Supermercado o tienda de alimentación', 'shopping-cart', TRUE),
    ('RESTAURANT',       'Restaurante',          'Restaurante o cafetería',           'utensils',    TRUE),
    ('PHARMACY',         'Farmacia',             'Farmacia o parafarmacia',           'pill',        TRUE),
    ('ELECTRONICS',      'Electrónica',          'Venta de electrónica y gadgets',   'cpu',         TRUE),
    ('BEAUTY_SALON',     'Peluquería / Estética','Salón de belleza o peluquería',    'scissors',    TRUE),
    ('GYM',              'Gimnasio',             'Gimnasio o centro deportivo',       'dumbbell',    TRUE),
    ('OTHER',            'Otro',                 'Tipo de negocio no listado',        'building',    TRUE);

-- ============================================================
-- companies (= tenants)
-- ============================================================
CREATE TABLE companies (
    id                      UUID              NOT NULL DEFAULT gen_random_uuid(),
    name                    VARCHAR(255)      NOT NULL,
    business_type_id        UUID,
    tax_id                  VARCHAR(50),
    phone                   VARCHAR(20),
    address                 TEXT,
    timezone                VARCHAR(50)       NOT NULL DEFAULT 'Europe/Madrid',
    logo_url                VARCHAR(500),
    plan                    subscription_plan NOT NULL DEFAULT 'TRIAL',
    plan_expires_at         TIMESTAMPTZ,
    stripe_customer_id      VARCHAR(100)      UNIQUE,
    stripe_subscription_id  VARCHAR(100)      UNIQUE,
    max_employees           INT               NOT NULL DEFAULT 3,
    max_branches            INT               NOT NULL DEFAULT 1,
    max_products            INT               NOT NULL DEFAULT 100,
    max_product_images      INT               NOT NULL DEFAULT 3,
    is_active               BOOLEAN           NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMPTZ       NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ       NOT NULL DEFAULT now(),

    CONSTRAINT pk_companies PRIMARY KEY (id),
    CONSTRAINT fk_companies_business_type FOREIGN KEY (business_type_id)
        REFERENCES business_types (id) ON DELETE SET NULL
);

CREATE INDEX idx_companies_stripe_customer ON companies (stripe_customer_id)
    WHERE stripe_customer_id IS NOT NULL;
CREATE INDEX idx_companies_plan            ON companies (plan);
CREATE INDEX idx_companies_active          ON companies (is_active);

-- RLS: una empresa solo se ve a sí misma
ALTER TABLE companies ENABLE ROW LEVEL SECURITY;

CREATE POLICY companies_tenant_isolation ON companies
    USING (id::text = current_setting('app.current_tenant', true));

-- ============================================================
-- branches (sucursales del tenant)
-- ============================================================
CREATE TABLE branches (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    tenant_id   UUID        NOT NULL,
    name        VARCHAR(255) NOT NULL,
    phone       VARCHAR(20),
    address     TEXT,
    is_main     BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_branches PRIMARY KEY (id),
    CONSTRAINT fk_branches_company FOREIGN KEY (tenant_id) REFERENCES companies (id) ON DELETE CASCADE
);

CREATE INDEX idx_branches_tenant_id ON branches (tenant_id);

ALTER TABLE branches ENABLE ROW LEVEL SECURITY;

CREATE POLICY branches_tenant_isolation ON branches
    USING (tenant_id::text = current_setting('app.current_tenant', true));
