-- ============================================================
-- V5 — Sales: customers + orders + order_items + refunds
-- ============================================================

CREATE TABLE customers (
    id          UUID            NOT NULL DEFAULT gen_random_uuid(),
    tenant_id   UUID            NOT NULL,
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    email       VARCHAR(150),
    phone       VARCHAR(20),
    notes       TEXT,
    total_spent NUMERIC(10,2)   NOT NULL DEFAULT 0,
    order_count INT             NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT pk_customers PRIMARY KEY (id)
);

CREATE INDEX idx_customers_tenant_id ON customers (tenant_id);
CREATE INDEX idx_customers_email     ON customers (tenant_id, email) WHERE email IS NOT NULL;
CREATE INDEX idx_customers_phone     ON customers (tenant_id, phone) WHERE phone IS NOT NULL;

ALTER TABLE customers ENABLE ROW LEVEL SECURITY;

CREATE POLICY customers_tenant_isolation ON customers
    USING (tenant_id::text = current_setting('app.current_tenant', true));

-- ============================================================

CREATE TABLE orders (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    tenant_id       UUID            NOT NULL,
    branch_id       UUID,
    order_number    VARCHAR(30)     NOT NULL,
    employee_id     UUID            NOT NULL,
    customer_id     UUID,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    subtotal        NUMERIC(10,2)   NOT NULL,
    discount_amount NUMERIC(10,2)   NOT NULL DEFAULT 0,
    tax_amount      NUMERIC(10,2)   NOT NULL DEFAULT 0,
    total           NUMERIC(10,2)   NOT NULL,
    payment_method  VARCHAR(30),
    notes           TEXT,
    completed_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT pk_orders             PRIMARY KEY (id),
    CONSTRAINT uq_orders_number      UNIQUE (tenant_id, order_number),
    CONSTRAINT ck_orders_status      CHECK (status IN ('PENDING','COMPLETED','CANCELLED','REFUNDED')),
    CONSTRAINT ck_orders_total       CHECK (total >= 0),
    CONSTRAINT fk_orders_customer    FOREIGN KEY (customer_id)
        REFERENCES customers (id) ON DELETE SET NULL
);

CREATE INDEX idx_orders_tenant_id   ON orders (tenant_id);
CREATE INDEX idx_orders_branch_id   ON orders (tenant_id, branch_id) WHERE branch_id IS NOT NULL;
CREATE INDEX idx_orders_customer_id ON orders (tenant_id, customer_id) WHERE customer_id IS NOT NULL;
CREATE INDEX idx_orders_status      ON orders (tenant_id, status);
CREATE INDEX idx_orders_created_at  ON orders (tenant_id, created_at DESC);

ALTER TABLE orders ENABLE ROW LEVEL SECURITY;

CREATE POLICY orders_tenant_isolation ON orders
    USING (tenant_id::text = current_setting('app.current_tenant', true));

-- ============================================================

CREATE TABLE order_items (
    id               UUID            NOT NULL DEFAULT gen_random_uuid(),
    order_id         UUID            NOT NULL,
    tenant_id        UUID            NOT NULL,
    product_id       UUID,
    variant_id       UUID,
    product_name     VARCHAR(200)    NOT NULL,
    quantity         NUMERIC(10,3)   NOT NULL,
    unit_price       NUMERIC(10,2)   NOT NULL,
    tax_rate         NUMERIC(5,2)    NOT NULL DEFAULT 0,
    discount_percent NUMERIC(5,2)    NOT NULL DEFAULT 0,
    line_total       NUMERIC(10,2)   NOT NULL,

    CONSTRAINT pk_order_items       PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id)
        REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT ck_order_items_qty   CHECK (quantity > 0)
);

CREATE INDEX idx_order_items_order_id  ON order_items (order_id);
CREATE INDEX idx_order_items_tenant_id ON order_items (tenant_id);

ALTER TABLE order_items ENABLE ROW LEVEL SECURITY;

CREATE POLICY order_items_tenant_isolation ON order_items
    USING (tenant_id::text = current_setting('app.current_tenant', true));

-- ============================================================

CREATE TABLE refunds (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    tenant_id       UUID            NOT NULL,
    order_id        UUID            NOT NULL,
    employee_id     UUID,
    refund_amount   NUMERIC(10,2)   NOT NULL,
    reason          TEXT,
    refund_type     VARCHAR(20),
    refund_method   VARCHAR(30),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT pk_refunds        PRIMARY KEY (id),
    CONSTRAINT fk_refunds_order  FOREIGN KEY (order_id)
        REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT ck_refunds_amount CHECK (refund_amount > 0)
);

CREATE INDEX idx_refunds_order_id  ON refunds (order_id);
CREATE INDEX idx_refunds_tenant_id ON refunds (tenant_id);

ALTER TABLE refunds ENABLE ROW LEVEL SECURITY;

CREATE POLICY refunds_tenant_isolation ON refunds
    USING (tenant_id::text = current_setting('app.current_tenant', true));
