-- ============================================================
-- V6 — Módulo de Gastos (bizcore-expenses)
-- ============================================================

CREATE TABLE IF NOT EXISTS expenses (
    id               UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id        UUID         NOT NULL,
    branch_id        UUID,
    expense_number   VARCHAR(30)  NOT NULL,
    category         VARCHAR(30)  NOT NULL,
    description      TEXT         NOT NULL,
    amount           NUMERIC(12,2) NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    due_date         DATE,
    paid_at          TIMESTAMPTZ,
    supplier_name    VARCHAR(255),
    attachment_url   TEXT,
    notes            TEXT,
    created_by_id    UUID         NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_expenses PRIMARY KEY (id),
    CONSTRAINT uq_expenses_number UNIQUE (tenant_id, expense_number),
    CONSTRAINT ck_expenses_amount CHECK (amount > 0),
    CONSTRAINT ck_expenses_category CHECK (category IN (
        'RENT','UTILITIES','SALARY','SUPPLIES','MAINTENANCE','MARKETING','TAXES','OTHER'
    )),
    CONSTRAINT ck_expenses_status CHECK (status IN ('PENDING','PAID','CANCELLED'))
);

CREATE INDEX idx_expenses_tenant_id     ON expenses (tenant_id);
CREATE INDEX idx_expenses_branch_id     ON expenses (tenant_id, branch_id) WHERE branch_id IS NOT NULL;
CREATE INDEX idx_expenses_status        ON expenses (tenant_id, status);
CREATE INDEX idx_expenses_category      ON expenses (tenant_id, category);
CREATE INDEX idx_expenses_due_date      ON expenses (tenant_id, due_date);
CREATE INDEX idx_expenses_created_at    ON expenses (tenant_id, created_at DESC);

-- RLS: cada tenant solo ve sus propios gastos
ALTER TABLE expenses ENABLE ROW LEVEL SECURITY;

CREATE POLICY expenses_tenant_isolation ON expenses
    USING (tenant_id::text = current_setting('app.current_tenant', true));
