-- ============================================================
-- V1 — Auth: users + refresh_tokens
-- ============================================================

-- Tipo PostgreSQL para roles
CREATE TYPE user_role AS ENUM (
    'OWNER', 'ADMIN', 'MANAGER', 'EMPLOYEE', 'VIEWER', 'SUPER_ADMIN'
);

CREATE TABLE users (
    id              UUID          NOT NULL DEFAULT gen_random_uuid(),
    tenant_id       UUID,
    email           VARCHAR(255)  NOT NULL,
    password_hash   VARCHAR(255)  NOT NULL,
    first_name      VARCHAR(100)  NOT NULL,
    last_name       VARCHAR(100)  NOT NULL,
    phone           VARCHAR(20),
    role            user_role     NOT NULL,
    is_active       BOOLEAN       NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX idx_users_tenant_id  ON users (tenant_id);
CREATE INDEX idx_users_email      ON users (email);
CREATE INDEX idx_users_role       ON users (tenant_id, role);

-- RLS: cada tenant ve solo sus usuarios
-- SUPER_ADMIN (tenant_id IS NULL) se gestiona fuera de RLS
ALTER TABLE users ENABLE ROW LEVEL SECURITY;

CREATE POLICY users_tenant_isolation ON users
    USING (
        tenant_id IS NULL
        OR tenant_id::text = current_setting('app.current_tenant', true)
    );

-- ============================================================

CREATE TABLE refresh_tokens (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL,
    token_hash  VARCHAR(255) NOT NULL,
    ip_address  INET,
    user_agent  TEXT,
    expires_at  TIMESTAMPTZ NOT NULL,
    revoked_at  TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uq_refresh_tokens_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id    ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);
