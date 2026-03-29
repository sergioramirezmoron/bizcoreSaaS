-- ============================================================
-- BizCore SaaS — Esquema de base de datos PostgreSQL 17
-- Multi-tenant con Row Level Security (RLS)
-- ============================================================
-- Orden de creación diseñado para respetar todas las FK.
-- La dependencia circular companies ↔ promo_codes se resuelve
-- creando companies sin promo_code_id y añadiéndolo vía ALTER TABLE
-- después de crear promo_codes.
-- ============================================================

-- ============================================================
-- EXTENSIONES
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";   -- gen_random_uuid() (ya nativo en PG17, pero por compatibilidad)
CREATE EXTENSION IF NOT EXISTS "pg_trgm";    -- búsqueda full-text por trigrama en productos/pedidos

-- ============================================================
-- SECCIÓN 1 — TABLAS DE PLATAFORMA (sin tenant)
-- ============================================================

-- ------------------------------------------------------------
-- Tipos de negocio / sectores
-- ------------------------------------------------------------
CREATE TABLE business_types (
    id                          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    code                        VARCHAR(50)  UNIQUE NOT NULL,
    name                        VARCHAR(100) NOT NULL,
    description                 TEXT,
    icon_key                    VARCHAR(50),
    default_category_template   JSONB,
    product_fields_config       JSONB,
    default_alerts_config       JSONB,
    is_active                   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at                  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- Empresas (tenants) — tabla central del sistema multi-tenant
-- NOTA: promo_code_id se añade vía ALTER TABLE después de
--       crear promo_codes para resolver la dependencia circular.
-- ------------------------------------------------------------
CREATE TABLE companies (
    id                      UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name                    VARCHAR(200) NOT NULL,
    business_type_id        UUID         REFERENCES business_types(id),
    tax_id                  VARCHAR(50),
    address                 TEXT,
    phone                   VARCHAR(20),
    email                   VARCHAR(150) NOT NULL,
    logo_url                TEXT,
    timezone                VARCHAR(50)  NOT NULL DEFAULT 'Europe/Madrid',
    currency                VARCHAR(10)  NOT NULL DEFAULT 'EUR',
    locale                  VARCHAR(10)  NOT NULL DEFAULT 'es-ES',

    -- Suscripción
    subscription_plan       VARCHAR(20)  NOT NULL DEFAULT 'TRIAL',
    subscription_status     VARCHAR(20)  NOT NULL DEFAULT 'TRIAL',
    trial_ends_at           TIMESTAMP,
    plan_override_by_admin  BOOLEAN      NOT NULL DEFAULT FALSE,
    admin_notes             TEXT,

    -- Stripe
    stripe_customer_id      VARCHAR(100) UNIQUE,
    stripe_subscription_id  VARCHAR(100) UNIQUE,

    -- Límites según plan (se actualizan al cambiar de plan)
    max_employees           INT          NOT NULL DEFAULT 3,
    max_branches            INT          NOT NULL DEFAULT 1,
    max_products            INT          NOT NULL DEFAULT 100,
    max_product_images      INT          NOT NULL DEFAULT 1,

    is_active               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_companies_subscription_plan
        CHECK (subscription_plan IN ('TRIAL', 'BASIC', 'STANDARD', 'PREMIUM')),
    CONSTRAINT chk_companies_subscription_status
        CHECK (subscription_status IN ('TRIAL', 'ACTIVE', 'PAST_DUE', 'CANCELLED', 'SUSPENDED')),
    CONSTRAINT chk_companies_max_employees   CHECK (max_employees   >= 0),
    CONSTRAINT chk_companies_max_branches    CHECK (max_branches    >= 0),
    CONSTRAINT chk_companies_max_products    CHECK (max_products    >= 0),
    CONSTRAINT chk_companies_max_images      CHECK (max_product_images >= 0)
);

-- ------------------------------------------------------------
-- Sucursales (branches)
-- tenant_id sin FK explícita a companies (patrón multi-tenant
-- con RLS; el aislamiento lo garantiza la política RLS).
-- ------------------------------------------------------------
CREATE TABLE branches (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL,
    name        VARCHAR(150) NOT NULL,
    address     TEXT,
    phone       VARCHAR(20),
    is_main     BOOLEAN      NOT NULL DEFAULT FALSE,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- Usuarios (empleados de empresa + SUPER_ADMIN de plataforma)
-- tenant_id = NULL  →  SUPER_ADMIN (sin empresa asociada)
-- Sin FK explícita a companies para permitir NULL en SUPER_ADMIN.
-- ------------------------------------------------------------
CREATE TABLE users (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID,                                    -- NULL = SUPER_ADMIN de plataforma
    email           VARCHAR(150) UNIQUE NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(20),
    avatar_url      TEXT,
    role            VARCHAR(30)  NOT NULL,
    branch_id       UUID         REFERENCES branches(id),
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_users_role
        CHECK (role IN ('OWNER', 'ADMIN', 'MANAGER', 'EMPLOYEE', 'VIEWER', 'SUPER_ADMIN')),
    CONSTRAINT chk_users_super_admin_no_tenant
        CHECK (
            (role = 'SUPER_ADMIN' AND tenant_id IS NULL)
            OR
            (role <> 'SUPER_ADMIN' AND tenant_id IS NOT NULL)
        )
);

-- ------------------------------------------------------------
-- Refresh tokens (JWT — rotación en cada uso, cookie HttpOnly)
-- ------------------------------------------------------------
CREATE TABLE refresh_tokens (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash  VARCHAR(255) UNIQUE NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    is_revoked  BOOLEAN      NOT NULL DEFAULT FALSE,
    ip_address  VARCHAR(45),
    user_agent  TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- Códigos promocionales
-- ------------------------------------------------------------
CREATE TABLE promo_codes (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    code                VARCHAR(50)  UNIQUE NOT NULL,
    description         TEXT,
    type                VARCHAR(30)  NOT NULL,
    value               JSONB        NOT NULL,
    -- TRIAL_EXTENSION:  {"days": 30}
    -- PERCENT_DISCOUNT: {"percent": 20, "duration_months": 3}
    -- FREE_MONTHS:      {"months": 2}
    -- PLAN_UPGRADE:     {"plan": "STANDARD", "price_of_plan": "BASIC"}
    applicable_plans    VARCHAR(20)[],                       -- NULL = aplica a todos los planes
    max_uses            INT,                                 -- NULL = ilimitado
    current_uses        INT          NOT NULL DEFAULT 0,
    valid_from          TIMESTAMP    NOT NULL DEFAULT NOW(),
    valid_until         TIMESTAMP,                           -- NULL = permanente
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    stripe_coupon_id    VARCHAR(100),
    created_by          UUID         REFERENCES users(id),
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_promo_codes_type
        CHECK (type IN ('TRIAL_EXTENSION', 'PERCENT_DISCOUNT', 'FREE_MONTHS', 'PLAN_UPGRADE')),
    CONSTRAINT chk_promo_codes_max_uses
        CHECK (max_uses IS NULL OR max_uses > 0),
    CONSTRAINT chk_promo_codes_current_uses
        CHECK (current_uses >= 0),
    CONSTRAINT chk_promo_codes_dates
        CHECK (valid_until IS NULL OR valid_until > valid_from)
);

-- Resolver dependencia circular: añadir promo_code_id a companies
-- una vez que promo_codes ya existe.
ALTER TABLE companies
    ADD COLUMN promo_code_id UUID REFERENCES promo_codes(id);

-- ------------------------------------------------------------
-- Historial de uso de códigos promocionales
-- ------------------------------------------------------------
CREATE TABLE promo_code_usages (
    id              UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    promo_code_id   UUID      NOT NULL REFERENCES promo_codes(id),
    company_id      UUID      NOT NULL REFERENCES companies(id),
    applied_at      TIMESTAMP NOT NULL DEFAULT NOW(),

    UNIQUE(promo_code_id, company_id)    -- cada empresa solo puede usar el mismo código una vez
);

-- ------------------------------------------------------------
-- Acciones manuales del superadmin (trazabilidad completa)
-- ------------------------------------------------------------
CREATE TABLE admin_actions (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id      UUID         NOT NULL REFERENCES companies(id),
    action_type     VARCHAR(50)  NOT NULL,
    description     TEXT         NOT NULL,
    value           JSONB,
    performed_by    UUID         NOT NULL REFERENCES users(id),
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_admin_actions_type
        CHECK (action_type IN (
            'FREE_MONTHS', 'PERCENT_DISCOUNT', 'PLAN_OVERRIDE', 'TRIAL_EXTEND',
            'SUSPEND', 'REACTIVATE', 'NOTE', 'PROMO_CODE_MANUAL'
        ))
);

-- ============================================================
-- SECCIÓN 2 — TABLAS DE NEGOCIO (datos de tenant)
-- Todas incluyen tenant_id NOT NULL con índice.
-- El aislamiento se garantiza mediante RLS (ver Sección 5).
-- ============================================================

-- ------------------------------------------------------------
-- Categorías de productos (jerarquía con auto-referencia)
-- ------------------------------------------------------------
CREATE TABLE categories (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL,
    parent_id   UUID         REFERENCES categories(id),
    name        VARCHAR(100) NOT NULL,
    color       VARCHAR(7),
    sort_order  INT          NOT NULL DEFAULT 0,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- Proveedores (plan Standard+)
-- ------------------------------------------------------------
CREATE TABLE suppliers (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL,
    name            VARCHAR(200) NOT NULL,
    contact_name    VARCHAR(150),
    email           VARCHAR(150),
    phone           VARCHAR(20),
    tax_id          VARCHAR(50),
    payment_terms   VARCHAR(100),
    notes           TEXT,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ------------------------------------------------------------
-- Clientes CRM (plan Premium)
-- ------------------------------------------------------------
CREATE TABLE customers (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL,
    first_name      VARCHAR(100),
    last_name       VARCHAR(100),
    email           VARCHAR(150),
    phone           VARCHAR(20),
    notes           TEXT,
    total_spent     DECIMAL(10,2) NOT NULL DEFAULT 0,
    order_count     INT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_customers_total_spent  CHECK (total_spent >= 0),
    CONSTRAINT chk_customers_order_count  CHECK (order_count >= 0)
);

-- ------------------------------------------------------------
-- Productos
-- custom_fields almacena los campos dinámicos según el sector
-- (generados a partir de business_types.product_fields_config)
-- ------------------------------------------------------------
CREATE TABLE products (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL,
    category_id     UUID          REFERENCES categories(id),
    sku             VARCHAR(100),
    name            VARCHAR(200)  NOT NULL,
    description     TEXT,
    purchase_price  DECIMAL(10,2),
    selling_price   DECIMAL(10,2) NOT NULL,
    tax_rate        DECIMAL(5,2)  NOT NULL DEFAULT 21.00,
    unit            VARCHAR(20)   NOT NULL DEFAULT 'ud',
    custom_fields   JSONB,
    is_active       BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_products_selling_price
        CHECK (selling_price >= 0),
    CONSTRAINT chk_products_purchase_price
        CHECK (purchase_price IS NULL OR purchase_price >= 0),
    CONSTRAINT chk_products_tax_rate
        CHECK (tax_rate >= 0 AND tax_rate <= 100),
    -- SKU único por tenant (solo cuando se proporciona)
    UNIQUE NULLS NOT DISTINCT (tenant_id, sku)
);

-- ------------------------------------------------------------
-- Imágenes de producto (almacenadas en Cloudflare R2)
-- sort_order 0 = imagen principal
-- ------------------------------------------------------------
CREATE TABLE product_images (
    id          UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id  UUID    NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    tenant_id   UUID    NOT NULL,
    image_url   TEXT    NOT NULL,
    r2_key      TEXT    NOT NULL UNIQUE,    -- clave en R2 para borrado futuro
    size_bytes  INT     NOT NULL,
    sort_order  INT     NOT NULL DEFAULT 0,
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_product_images_size CHECK (size_bytes > 0)
);

-- ------------------------------------------------------------
-- Variantes de producto (tallas, colores, etc.)
-- ------------------------------------------------------------
CREATE TABLE product_variants (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id      UUID          NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    tenant_id       UUID          NOT NULL,
    sku             VARCHAR(100),
    variant_name    VARCHAR(100),
    attributes      JSONB,                   -- {"color": "Azul", "size": "M"}
    selling_price   DECIMAL(10,2),
    is_active       BOOLEAN       NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_product_variants_selling_price
        CHECK (selling_price IS NULL OR selling_price >= 0),
    UNIQUE NULLS NOT DISTINCT (tenant_id, sku)
);

-- ------------------------------------------------------------
-- Stock por producto/variante/sucursal
-- UNIQUE NULLS NOT DISTINCT garantiza un único registro
-- de stock para cada combinación (incluyendo NULLs).
-- ------------------------------------------------------------
CREATE TABLE stock_items (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL,
    product_id      UUID          REFERENCES products(id),
    variant_id      UUID          REFERENCES product_variants(id),
    branch_id       UUID          REFERENCES branches(id),
    quantity        DECIMAL(10,3) NOT NULL DEFAULT 0,
    min_quantity    DECIMAL(10,3) NOT NULL DEFAULT 0,
    max_quantity    DECIMAL(10,3),
    location        VARCHAR(100),
    last_updated_at TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_stock_items_quantity     CHECK (quantity >= 0),
    CONSTRAINT chk_stock_items_min_quantity CHECK (min_quantity >= 0),
    CONSTRAINT chk_stock_items_max_quantity
        CHECK (max_quantity IS NULL OR max_quantity >= min_quantity),
    CONSTRAINT chk_stock_items_product_or_variant
        CHECK (product_id IS NOT NULL OR variant_id IS NOT NULL),
    UNIQUE NULLS NOT DISTINCT (tenant_id, product_id, variant_id, branch_id)
);

-- ------------------------------------------------------------
-- Historial de movimientos de stock
-- ------------------------------------------------------------
CREATE TABLE stock_movements (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL,
    stock_item_id   UUID          NOT NULL REFERENCES stock_items(id),
    movement_type   VARCHAR(30)   NOT NULL,
    quantity_change DECIMAL(10,3) NOT NULL,
    quantity_before DECIMAL(10,3) NOT NULL,
    quantity_after  DECIMAL(10,3) NOT NULL,
    reference_id    UUID,                    -- ID del pedido/compra/ajuste origen
    notes           TEXT,
    created_by      UUID          REFERENCES users(id),
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_stock_movements_type
        CHECK (movement_type IN ('ORDER', 'PURCHASE', 'ADJUSTMENT', 'RETURN', 'TRANSFER'))
);

-- ------------------------------------------------------------
-- Pedidos registrados por los empleados
-- order_number es único por tenant: ORD-2025-00342
-- ------------------------------------------------------------
CREATE TABLE orders (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL,
    branch_id       UUID          REFERENCES branches(id),
    order_number    VARCHAR(30)   NOT NULL,
    employee_id     UUID          NOT NULL REFERENCES users(id),
    customer_id     UUID          REFERENCES customers(id),
    status          VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    subtotal        DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    tax_amount      DECIMAL(10,2) NOT NULL,
    total           DECIMAL(10,2) NOT NULL,
    payment_method  VARCHAR(30),
    notes           TEXT,
    completed_at    TIMESTAMP,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),

    UNIQUE(tenant_id, order_number),

    CONSTRAINT chk_orders_status
        CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED', 'REFUNDED')),
    CONSTRAINT chk_orders_payment_method
        CHECK (payment_method IS NULL
            OR payment_method IN ('CASH', 'CARD', 'TRANSFER', 'PENDING', 'OTHER')),
    CONSTRAINT chk_orders_subtotal          CHECK (subtotal         >= 0),
    CONSTRAINT chk_orders_discount_amount   CHECK (discount_amount  >= 0),
    CONSTRAINT chk_orders_tax_amount        CHECK (tax_amount       >= 0),
    CONSTRAINT chk_orders_total             CHECK (total            >= 0),
    CONSTRAINT chk_orders_completed_at
        CHECK (completed_at IS NULL OR status IN ('COMPLETED', 'REFUNDED'))
);

-- ------------------------------------------------------------
-- Líneas de pedido
-- product_name se guarda como snapshot del momento del pedido
-- (el producto puede cambiar de nombre o precio después)
-- ------------------------------------------------------------
CREATE TABLE order_items (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id         UUID          NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    tenant_id        UUID          NOT NULL,
    product_id       UUID          REFERENCES products(id),
    variant_id       UUID          REFERENCES product_variants(id),
    product_name     VARCHAR(200)  NOT NULL,
    quantity         DECIMAL(10,3) NOT NULL,
    unit_price       DECIMAL(10,2) NOT NULL,
    tax_rate         DECIMAL(5,2)  NOT NULL,
    discount_percent DECIMAL(5,2)  NOT NULL DEFAULT 0,
    line_total       DECIMAL(10,2) NOT NULL,

    CONSTRAINT chk_order_items_quantity         CHECK (quantity          > 0),
    CONSTRAINT chk_order_items_unit_price       CHECK (unit_price        >= 0),
    CONSTRAINT chk_order_items_tax_rate         CHECK (tax_rate          >= 0 AND tax_rate <= 100),
    CONSTRAINT chk_order_items_discount_percent CHECK (discount_percent  >= 0 AND discount_percent <= 100),
    CONSTRAINT chk_order_items_line_total       CHECK (line_total        >= 0)
);

-- ------------------------------------------------------------
-- Devoluciones (totales o parciales)
-- ------------------------------------------------------------
CREATE TABLE refunds (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL,
    order_id        UUID          NOT NULL REFERENCES orders(id),
    employee_id     UUID          REFERENCES users(id),
    refund_amount   DECIMAL(10,2) NOT NULL,
    reason          TEXT,
    refund_type     VARCHAR(20),
    refund_method   VARCHAR(30),
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_refunds_amount CHECK (refund_amount > 0),
    CONSTRAINT chk_refunds_type
        CHECK (refund_type IS NULL OR refund_type IN ('FULL', 'PARTIAL'))
);

-- ------------------------------------------------------------
-- Categorías de gastos
-- ------------------------------------------------------------
CREATE TABLE expense_categories (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    color       VARCHAR(7),
    is_system   BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ------------------------------------------------------------
-- Gastos del negocio (alquiler, proveedores, sueldos, etc.)
-- ------------------------------------------------------------
CREATE TABLE expenses (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL,
    branch_id       UUID          REFERENCES branches(id),
    category_id     UUID          REFERENCES expense_categories(id),
    supplier_id     UUID          REFERENCES suppliers(id),
    amount          DECIMAL(10,2) NOT NULL,
    tax_amount      DECIMAL(10,2) NOT NULL DEFAULT 0,
    description     TEXT          NOT NULL,
    expense_date    DATE          NOT NULL,
    payment_method  VARCHAR(30),
    receipt_url     TEXT,
    status          VARCHAR(20)   NOT NULL DEFAULT 'PAID',
    created_by      UUID          REFERENCES users(id),
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_expenses_amount     CHECK (amount     > 0),
    CONSTRAINT chk_expenses_tax_amount CHECK (tax_amount >= 0),
    CONSTRAINT chk_expenses_status     CHECK (status IN ('PAID', 'PENDING'))
);

-- ------------------------------------------------------------
-- Configuraciones de alertas por tenant
-- notify_roles: array de roles que reciben la notificación
-- ------------------------------------------------------------
CREATE TABLE alert_configs (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL,
    alert_type      VARCHAR(50)   NOT NULL,
    is_enabled      BOOLEAN       NOT NULL DEFAULT TRUE,
    threshold_value DECIMAL(10,2),
    notify_email    BOOLEAN       NOT NULL DEFAULT TRUE,
    notify_whatsapp BOOLEAN       NOT NULL DEFAULT FALSE,
    notify_roles    VARCHAR(30)[],
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),

    UNIQUE(tenant_id, alert_type)
);

-- ------------------------------------------------------------
-- Notificaciones de alertas generadas
-- ------------------------------------------------------------
CREATE TABLE alert_notifications (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL,
    alert_type      VARCHAR(50)  NOT NULL,
    title           VARCHAR(200) NOT NULL,
    message         TEXT         NOT NULL,
    reference_id    UUID,
    is_read         BOOLEAN      NOT NULL DEFAULT FALSE,
    sent_at         TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- API Keys para integraciones externas (plan Premium)
-- Solo se almacena el hash de la clave, nunca el texto plano.
-- key_prefix: primeros 8 caracteres para identificación en UI.
-- ------------------------------------------------------------
CREATE TABLE api_keys (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id    UUID         NOT NULL,
    key_hash     VARCHAR(255) UNIQUE NOT NULL,
    key_prefix   VARCHAR(10)  NOT NULL,
    name         VARCHAR(100) NOT NULL,
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    last_used_at TIMESTAMP,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- Logs de auditoría (AOP en Spring — ver sección 9.9)
-- old_value / new_value almacenan el estado antes/después
-- ------------------------------------------------------------
CREATE TABLE audit_logs (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL,
    user_id     UUID         REFERENCES users(id),
    action      VARCHAR(50)  NOT NULL,
    entity_type VARCHAR(50)  NOT NULL,
    entity_id   UUID,
    old_value   JSONB,
    new_value   JSONB,
    ip_address  VARCHAR(45),
    user_agent  TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- SECCIÓN 3 — ÍNDICES
-- ============================================================

-- Índices explícitos del README (sección 14)
CREATE INDEX idx_orders_tenant_date      ON orders(tenant_id, created_at DESC);
CREATE INDEX idx_products_tenant         ON products(tenant_id);
CREATE INDEX idx_stock_items_product     ON stock_items(product_id, branch_id);
CREATE INDEX idx_audit_tenant_date       ON audit_logs(tenant_id, created_at DESC);
CREATE INDEX idx_companies_stripe        ON companies(stripe_customer_id);
CREATE INDEX idx_promo_codes_code        ON promo_codes(code) WHERE is_active = TRUE;
CREATE INDEX idx_product_images_product  ON product_images(product_id, sort_order);

-- Índices adicionales necesarios para producción
CREATE INDEX idx_users_tenant            ON users(tenant_id);
CREATE INDEX idx_users_email             ON users(email);

CREATE INDEX idx_companies_status        ON companies(subscription_status);
CREATE INDEX idx_companies_trial_ends    ON companies(trial_ends_at) WHERE subscription_status = 'TRIAL';

CREATE INDEX idx_branches_tenant         ON branches(tenant_id);

CREATE INDEX idx_refresh_tokens_user     ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires  ON refresh_tokens(expires_at) WHERE is_revoked = FALSE;

CREATE INDEX idx_promo_code_usages_code     ON promo_code_usages(promo_code_id);
CREATE INDEX idx_promo_code_usages_company  ON promo_code_usages(company_id);

CREATE INDEX idx_admin_actions_company   ON admin_actions(company_id, created_at DESC);

CREATE INDEX idx_categories_tenant       ON categories(tenant_id);
CREATE INDEX idx_categories_parent       ON categories(parent_id) WHERE parent_id IS NOT NULL;

CREATE INDEX idx_suppliers_tenant        ON suppliers(tenant_id);
CREATE INDEX idx_customers_tenant        ON customers(tenant_id);

CREATE INDEX idx_products_category       ON products(category_id);
CREATE INDEX idx_products_active         ON products(tenant_id, is_active);
-- Índice trigrama para búsqueda por nombre de producto (autocompletado)
CREATE INDEX idx_products_name_trgm      ON products USING gin(name gin_trgm_ops);

CREATE INDEX idx_product_images_tenant   ON product_images(tenant_id);
CREATE INDEX idx_product_variants_product ON product_variants(product_id);
CREATE INDEX idx_product_variants_tenant  ON product_variants(tenant_id);

CREATE INDEX idx_stock_items_tenant      ON stock_items(tenant_id);
CREATE INDEX idx_stock_items_variant     ON stock_items(variant_id);
CREATE INDEX idx_stock_items_low_stock   ON stock_items(tenant_id)
    WHERE quantity <= min_quantity;

CREATE INDEX idx_stock_movements_tenant  ON stock_movements(tenant_id, created_at DESC);
CREATE INDEX idx_stock_movements_item    ON stock_movements(stock_item_id);

CREATE INDEX idx_orders_status           ON orders(tenant_id, status);
CREATE INDEX idx_orders_employee         ON orders(employee_id);
CREATE INDEX idx_orders_customer         ON orders(customer_id) WHERE customer_id IS NOT NULL;
CREATE INDEX idx_orders_branch           ON orders(branch_id) WHERE branch_id IS NOT NULL;

CREATE INDEX idx_order_items_order       ON order_items(order_id);
CREATE INDEX idx_order_items_product     ON order_items(product_id) WHERE product_id IS NOT NULL;
CREATE INDEX idx_order_items_tenant      ON order_items(tenant_id);

CREATE INDEX idx_refunds_order           ON refunds(order_id);
CREATE INDEX idx_refunds_tenant          ON refunds(tenant_id);

CREATE INDEX idx_expense_categories_tenant ON expense_categories(tenant_id);

CREATE INDEX idx_expenses_tenant_date    ON expenses(tenant_id, expense_date DESC);
CREATE INDEX idx_expenses_category       ON expenses(category_id);
CREATE INDEX idx_expenses_supplier       ON expenses(supplier_id) WHERE supplier_id IS NOT NULL;

CREATE INDEX idx_alert_configs_tenant    ON alert_configs(tenant_id);

CREATE INDEX idx_alert_notifications_tenant ON alert_notifications(tenant_id, is_read, sent_at DESC);

CREATE INDEX idx_api_keys_tenant         ON api_keys(tenant_id);

CREATE INDEX idx_audit_logs_user         ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity       ON audit_logs(entity_type, entity_id);

-- ============================================================
-- SECCIÓN 4 — FUNCIONES Y TRIGGERS
-- ============================================================

-- ------------------------------------------------------------
-- Función: auto-actualizar updated_at en cada UPDATE
-- ------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_companies_updated_at
    BEFORE UPDATE ON companies
    FOR EACH ROW EXECUTE FUNCTION fn_update_updated_at();

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION fn_update_updated_at();

CREATE TRIGGER trg_products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION fn_update_updated_at();

CREATE TRIGGER trg_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION fn_update_updated_at();

-- ------------------------------------------------------------
-- Función: obtener el tenant_id actual del JWT (para RLS)
-- Retorna NULL si no hay tenant configurado (p.ej. SUPER_ADMIN).
-- El TenantFilter de Spring ejecuta:
--   SET LOCAL app.current_tenant = '<uuid>';
-- antes de cada consulta autenticada.
-- ------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_current_tenant_id()
RETURNS UUID AS $$
DECLARE
    v_setting TEXT;
BEGIN
    v_setting := current_setting('app.current_tenant', TRUE);
    IF v_setting IS NULL OR v_setting = '' THEN
        RETURN NULL;
    END IF;
    RETURN v_setting::UUID;
EXCEPTION
    WHEN invalid_text_representation THEN
        RETURN NULL;
END;
$$ LANGUAGE plpgsql STABLE SECURITY DEFINER;

-- ============================================================
-- SECCIÓN 5 — ROW LEVEL SECURITY (RLS)
-- Garantiza aislamiento de datos entre tenants como segunda
-- barrera de seguridad, incluso ante errores en la capa app.
-- El SUPER_ADMIN usa un rol con BYPASSRLS para operaciones
-- de soporte cross-tenant.
-- ============================================================

ALTER TABLE branches            ENABLE ROW LEVEL SECURITY;
ALTER TABLE categories          ENABLE ROW LEVEL SECURITY;
ALTER TABLE suppliers           ENABLE ROW LEVEL SECURITY;
ALTER TABLE customers           ENABLE ROW LEVEL SECURITY;
ALTER TABLE products            ENABLE ROW LEVEL SECURITY;
ALTER TABLE product_images      ENABLE ROW LEVEL SECURITY;
ALTER TABLE product_variants    ENABLE ROW LEVEL SECURITY;
ALTER TABLE stock_items         ENABLE ROW LEVEL SECURITY;
ALTER TABLE stock_movements     ENABLE ROW LEVEL SECURITY;
ALTER TABLE orders              ENABLE ROW LEVEL SECURITY;
ALTER TABLE order_items         ENABLE ROW LEVEL SECURITY;
ALTER TABLE refunds             ENABLE ROW LEVEL SECURITY;
ALTER TABLE expense_categories  ENABLE ROW LEVEL SECURITY;
ALTER TABLE expenses            ENABLE ROW LEVEL SECURITY;
ALTER TABLE alert_configs       ENABLE ROW LEVEL SECURITY;
ALTER TABLE alert_notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE api_keys            ENABLE ROW LEVEL SECURITY;
ALTER TABLE audit_logs          ENABLE ROW LEVEL SECURITY;

-- Política genérica: solo ver/modificar datos del propio tenant
CREATE POLICY tenant_isolation ON branches
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON categories
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON suppliers
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON customers
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON products
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON product_images
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON product_variants
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON stock_items
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON stock_movements
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON orders
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON order_items
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON refunds
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON expense_categories
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON expenses
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON alert_configs
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON alert_notifications
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON api_keys
    USING (tenant_id = fn_current_tenant_id());

CREATE POLICY tenant_isolation ON audit_logs
    USING (tenant_id = fn_current_tenant_id());

-- ============================================================
-- SECCIÓN 6 — DATOS INICIALES (seed de plataforma)
-- ============================================================

-- Tipos de negocio v1.0 (sección 3 del README)
INSERT INTO business_types (code, name, description, icon_key, product_fields_config, default_alerts_config) VALUES

('RETAIL_FASHION',
 'Tienda de ropa y moda',
 'Boutiques, tiendas de ropa, calzado',
 'shirt',
 '{
   "fields": [
     {"key": "size",     "label": "Talla",               "type": "select",
      "options": ["XS","S","M","L","XL","XXL"],             "required": false},
     {"key": "color",    "label": "Color",               "type": "text",   "required": false},
     {"key": "season",   "label": "Temporada",           "type": "select",
      "options": ["Primavera/Verano","Otoño/Invierno","Todo el año"], "required": false},
     {"key": "gender",   "label": "Género",              "type": "select",
      "options": ["Mujer","Hombre","Unisex","Niño"],         "required": false},
     {"key": "material", "label": "Material/Composición","type": "text",   "required": false}
   ]
 }'::JSONB,
 '{"low_stock_threshold": 5, "daily_sales_goal": null}'::JSONB),

('BAKERY_FOOD',
 'Panadería y alimentación',
 'Panaderías, pastelerías, colmados',
 'bread-slice',
 '{
   "fields": [
     {"key": "weight",      "label": "Peso",          "type": "text",   "required": false},
     {"key": "allergens",   "label": "Alérgenos",     "type": "text",   "required": false},
     {"key": "expiry_days", "label": "Días de caducidad", "type": "number", "required": false},
     {"key": "unit_type",   "label": "Unidad",        "type": "select",
      "options": ["ud", "kg", "g"],                                       "required": false}
   ]
 }'::JSONB,
 '{"low_stock_threshold": 3, "daily_sales_goal": null}'::JSONB),

('HARDWARE',
 'Ferretería y materiales',
 'Ferreterías, fontanería, bricolaje',
 'wrench',
 '{
   "fields": [
     {"key": "technical_ref", "label": "Referencia técnica", "type": "text", "required": false},
     {"key": "unit_type",     "label": "Unidad", "type": "select",
      "options": ["ud", "m", "kg"],                                          "required": false}
   ]
 }'::JSONB,
 '{"low_stock_threshold": 5, "daily_sales_goal": null}'::JSONB),

('PHARMACY',
 'Farmacia y parafarmacia',
 'Farmacias, herbolarios',
 'pill',
 '{
   "fields": [
     {"key": "active_ingredient", "label": "Principio activo",  "type": "text", "required": false},
     {"key": "national_code",     "label": "Código nacional",   "type": "text", "required": false}
   ]
 }'::JSONB,
 '{"low_stock_threshold": 5, "daily_sales_goal": null}'::JSONB),

('BEAUTY',
 'Belleza y estética',
 'Peluquerías, centros de estética',
 'scissors',
 '{
   "fields": [
     {"key": "item_type", "label": "Tipo", "type": "select",
      "options": ["Servicio", "Producto"],                    "required": false}
   ]
 }'::JSONB,
 '{"low_stock_threshold": 3, "daily_sales_goal": null}'::JSONB),

('GENERIC',
 'Comercio genérico',
 'Cualquier negocio no clasificado',
 'store',
 '{"fields": []}'::JSONB,
 '{"low_stock_threshold": 5, "daily_sales_goal": null}'::JSONB);
