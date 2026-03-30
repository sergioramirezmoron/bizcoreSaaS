-- ============================================================
-- V4 — Inventory: stock_items + stock_movements
-- ============================================================

CREATE TABLE stock_items (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    tenant_id       UUID            NOT NULL,
    product_id      UUID,
    variant_id      UUID,
    branch_id       UUID,
    quantity        NUMERIC(10,3)   NOT NULL DEFAULT 0,
    min_quantity    NUMERIC(10,3)   NOT NULL DEFAULT 0,
    max_quantity    NUMERIC(10,3),
    location        VARCHAR(100),
    last_updated_at TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT pk_stock_items PRIMARY KEY (id),
    CONSTRAINT ck_stock_items_quantity CHECK (quantity >= 0),

    -- Clave de negocio: una fila por (tenant, producto, variante, sucursal)
    -- NULLS NOT DISTINCT trata NULL como igual en la unicidad (PostgreSQL 15+)
    CONSTRAINT uq_stock_items_key UNIQUE NULLS NOT DISTINCT
        (tenant_id, product_id, variant_id, branch_id)
);

CREATE INDEX idx_stock_items_tenant_id  ON stock_items (tenant_id);
CREATE INDEX idx_stock_items_product_id ON stock_items (tenant_id, product_id);
CREATE INDEX idx_stock_items_low_stock  ON stock_items (tenant_id, quantity, min_quantity)
    WHERE quantity <= min_quantity;

ALTER TABLE stock_items ENABLE ROW LEVEL SECURITY;

CREATE POLICY stock_items_tenant_isolation ON stock_items
    USING (tenant_id::text = current_setting('app.current_tenant', true));

-- ============================================================

CREATE TABLE stock_movements (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    tenant_id       UUID            NOT NULL,
    stock_item_id   UUID            NOT NULL,
    movement_type   VARCHAR(30)     NOT NULL,
    quantity_change NUMERIC(10,3)   NOT NULL,
    quantity_before NUMERIC(10,3)   NOT NULL,
    quantity_after  NUMERIC(10,3)   NOT NULL,
    reference_id    UUID,
    notes           TEXT,
    created_by      UUID,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT pk_stock_movements          PRIMARY KEY (id),
    CONSTRAINT fk_stock_movements_item     FOREIGN KEY (stock_item_id)
        REFERENCES stock_items (id) ON DELETE CASCADE,
    CONSTRAINT ck_stock_movements_type     CHECK (movement_type IN (
        'ORDER', 'PURCHASE', 'ADJUSTMENT', 'RETURN', 'TRANSFER'
    ))
);

CREATE INDEX idx_stock_movements_tenant_id    ON stock_movements (tenant_id);
CREATE INDEX idx_stock_movements_item_id      ON stock_movements (stock_item_id);
CREATE INDEX idx_stock_movements_created_at   ON stock_movements (tenant_id, created_at DESC);
CREATE INDEX idx_stock_movements_reference_id ON stock_movements (reference_id)
    WHERE reference_id IS NOT NULL;

ALTER TABLE stock_movements ENABLE ROW LEVEL SECURITY;

CREATE POLICY stock_movements_tenant_isolation ON stock_movements
    USING (tenant_id::text = current_setting('app.current_tenant', true));
