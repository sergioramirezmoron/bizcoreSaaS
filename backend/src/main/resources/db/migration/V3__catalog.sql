-- ============================================================
-- V3 — Catalog: categories + products + images + variants
-- ============================================================

CREATE TABLE categories (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    tenant_id   UUID        NOT NULL,
    name        VARCHAR(100) NOT NULL,
    parent_id   UUID,
    color       VARCHAR(7),
    sort_order  INT         NOT NULL DEFAULT 0,
    is_active   BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id)
        REFERENCES categories (id) ON DELETE SET NULL
);

CREATE INDEX idx_categories_tenant_id  ON categories (tenant_id);
CREATE INDEX idx_categories_parent_id  ON categories (tenant_id, parent_id);
CREATE INDEX idx_categories_sort_order ON categories (tenant_id, sort_order);

ALTER TABLE categories ENABLE ROW LEVEL SECURITY;

CREATE POLICY categories_tenant_isolation ON categories
    USING (tenant_id::text = current_setting('app.current_tenant', true));

-- ============================================================

CREATE TABLE products (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    tenant_id       UUID            NOT NULL,
    category_id     UUID,
    name            VARCHAR(200)    NOT NULL,
    description     TEXT,
    sku             VARCHAR(100),
    purchase_price  NUMERIC(12,2),
    selling_price   NUMERIC(12,2)   NOT NULL,
    tax_rate        NUMERIC(5,2),
    unit            VARCHAR(50),
    custom_fields   JSONB,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id)
        REFERENCES categories (id) ON DELETE SET NULL,
    CONSTRAINT ck_products_selling_price CHECK (selling_price >= 0)
);

CREATE INDEX idx_products_tenant_id   ON products (tenant_id);
CREATE INDEX idx_products_category_id ON products (tenant_id, category_id);
CREATE INDEX idx_products_sku         ON products (tenant_id, sku) WHERE sku IS NOT NULL;
CREATE INDEX idx_products_active      ON products (tenant_id, is_active);
CREATE INDEX idx_products_custom_fields ON products USING GIN (custom_fields)
    WHERE custom_fields IS NOT NULL;

ALTER TABLE products ENABLE ROW LEVEL SECURITY;

CREATE POLICY products_tenant_isolation ON products
    USING (tenant_id::text = current_setting('app.current_tenant', true));

-- ============================================================

CREATE TABLE product_images (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    tenant_id   UUID        NOT NULL,
    product_id  UUID        NOT NULL,
    r2_key      VARCHAR(500) NOT NULL,
    image_url   TEXT        NOT NULL,
    size_bytes  INT         NOT NULL,
    sort_order  INT         NOT NULL DEFAULT 0,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_product_images      PRIMARY KEY (id),
    CONSTRAINT uq_product_images_r2   UNIQUE (r2_key),
    CONSTRAINT fk_product_images_prod FOREIGN KEY (product_id)
        REFERENCES products (id) ON DELETE CASCADE
);

CREATE INDEX idx_product_images_product_id ON product_images (product_id);
CREATE INDEX idx_product_images_tenant_id  ON product_images (tenant_id);

ALTER TABLE product_images ENABLE ROW LEVEL SECURITY;

CREATE POLICY product_images_tenant_isolation ON product_images
    USING (tenant_id::text = current_setting('app.current_tenant', true));

-- ============================================================

CREATE TABLE product_variants (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    tenant_id       UUID            NOT NULL,
    product_id      UUID            NOT NULL,
    variant_name    VARCHAR(100),
    sku             VARCHAR(100),
    attributes      JSONB,
    selling_price   NUMERIC(12,2),
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_product_variants      PRIMARY KEY (id),
    CONSTRAINT fk_product_variants_prod FOREIGN KEY (product_id)
        REFERENCES products (id) ON DELETE CASCADE
);

CREATE INDEX idx_product_variants_product_id ON product_variants (product_id);
CREATE INDEX idx_product_variants_tenant_id  ON product_variants (tenant_id);
CREATE INDEX idx_product_variants_sku        ON product_variants (tenant_id, sku)
    WHERE sku IS NOT NULL;

ALTER TABLE product_variants ENABLE ROW LEVEL SECURITY;

CREATE POLICY product_variants_tenant_isolation ON product_variants
    USING (tenant_id::text = current_setting('app.current_tenant', true));
