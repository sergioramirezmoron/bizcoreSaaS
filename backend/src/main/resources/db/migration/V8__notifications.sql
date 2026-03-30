-- ============================================================
-- V8 — Notifications: notification_logs
-- ============================================================

CREATE TABLE notification_logs (
    id            UUID        NOT NULL DEFAULT gen_random_uuid(),
    tenant_id     UUID        NOT NULL,
    type          VARCHAR(30) NOT NULL,
    channel       VARCHAR(20) NOT NULL,
    recipient     VARCHAR(255) NOT NULL,
    status        VARCHAR(10) NOT NULL,
    error_message TEXT,
    sent_at       TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_notification_logs PRIMARY KEY (id),
    CONSTRAINT ck_notification_logs_type CHECK (type IN (
        'WELCOME', 'SUBSCRIPTION_ACTIVATED', 'SUBSCRIPTION_CANCELLED',
        'PAYMENT_FAILED', 'LOW_STOCK', 'ORDER_RECEIPT', 'EMPLOYEE_INVITED'
    )),
    CONSTRAINT ck_notification_logs_channel CHECK (channel IN ('EMAIL', 'WHATSAPP')),
    CONSTRAINT ck_notification_logs_status  CHECK (status IN ('SENT', 'FAILED'))
);

CREATE INDEX idx_notification_logs_tenant_id ON notification_logs (tenant_id);
CREATE INDEX idx_notification_logs_sent_at   ON notification_logs (tenant_id, sent_at DESC);
CREATE INDEX idx_notification_logs_type      ON notification_logs (tenant_id, type);
CREATE INDEX idx_notification_logs_status    ON notification_logs (tenant_id, status);

ALTER TABLE notification_logs ENABLE ROW LEVEL SECURITY;

CREATE POLICY notification_logs_tenant_isolation ON notification_logs
    USING (tenant_id::text = current_setting('app.current_tenant', true));
