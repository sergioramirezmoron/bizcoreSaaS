-- ============================================================
-- V10 — Suppliers (proveedores) — Plan Standard+
-- ============================================================

CREATE TABLE suppliers (
    id              UUID          NOT NULL DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL,
    name            VARCHAR(200)  NOT NULL,
    contact_name    VARCHAR(100),
    email           VARCHAR(150),
    phone           VARCHAR(20),
    address         TEXT,
    website         VARCHAR(255),
    notes           TEXT,
    is_active       BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_suppliers PRIMARY KEY (id)
);

CREATE INDEX idx_suppliers_tenant        ON suppliers (tenant_id);
CREATE INDEX idx_suppliers_tenant_active ON suppliers (tenant_id, is_active);

ALTER TABLE suppliers ENABLE ROW LEVEL SECURITY;
CREATE POLICY suppliers_tenant_isolation ON suppliers
    USING (tenant_id::text = current_setting('app.current_tenant', true));
