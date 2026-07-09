-- V14__outbox_and_tenants.sql
-- Sprint 0: tenants (BC1), outbox_events + processed_events (BC9)
-- トランザクショナルアウトボックス (§9.1), RLS テナント分離 (§11.2 SEC-02)
-- UUID PK 採用（分散テナント識別子・グローバルイベントID）

-- 1. テナント登録
CREATE TABLE IF NOT EXISTS app.tenants (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(200) NOT NULL,
    slug        VARCHAR(50)  NOT NULL UNIQUE,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE','SUSPENDED','DELETED')),
    settings    JSONB        NOT NULL DEFAULT '{}',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by  VARCHAR(50)  NOT NULL,
    updated_by  VARCHAR(50)  NOT NULL,
    version     BIGINT       NOT NULL DEFAULT 0
);

-- 2. トランザクショナルアウトボックス (§9.1)
CREATE TABLE IF NOT EXISTS app.outbox_events (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id      UUID          NOT NULL REFERENCES app.tenants(id),
    aggregate_type VARCHAR(100)  NOT NULL,
    aggregate_id   UUID          NOT NULL,
    event_type     VARCHAR(200)  NOT NULL,
    payload        JSONB         NOT NULL,
    status         VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING','PUBLISHED','FAILED','SKIPPED')),
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    published_at   TIMESTAMPTZ,
    retry_count    INT           NOT NULL DEFAULT 0,
    last_error     TEXT
);

-- 3. 処理済みイベント（冪等性保証 §9.1）
CREATE TABLE IF NOT EXISTS app.processed_events (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL REFERENCES app.tenants(id),
    event_id        UUID          NOT NULL,
    consumer_id     VARCHAR(100)  NOT NULL,
    processed_at    TIMESTAMPTZ   NOT NULL DEFAULT now(),
    UNIQUE (event_id, consumer_id)
);

-- Indexes
CREATE INDEX idx_outbox_events_status ON app.outbox_events(status, created_at) WHERE status = 'PENDING';
CREATE INDEX idx_outbox_events_tenant ON app.outbox_events(tenant_id);
CREATE INDEX idx_processed_events_dedup ON app.processed_events(event_id, consumer_id);

-- RLS: テナント分離 (§11.2 SEC-02)
ALTER TABLE app.tenants          ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.tenants          FORCE  ROW LEVEL SECURITY;
ALTER TABLE app.outbox_events    ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.outbox_events    FORCE  ROW LEVEL SECURITY;
ALTER TABLE app.processed_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.processed_events FORCE  ROW LEVEL SECURITY;

-- ポリシー: セッション変数 app.current_tenant_id でフィルタ
CREATE POLICY tenants_isolation ON app.tenants
    USING (
        NULLIF(current_setting('app.current_tenant_id', true), '') IS NULL
        OR id = current_setting('app.current_tenant_id', true)::uuid
    );

CREATE POLICY outbox_tenant_isolation ON app.outbox_events
    USING (
        NULLIF(current_setting('app.current_tenant_id', true), '') IS NULL
        OR tenant_id = current_setting('app.current_tenant_id', true)::uuid
    );

CREATE POLICY processed_tenant_isolation ON app.processed_events
    USING (
        NULLIF(current_setting('app.current_tenant_id', true), '') IS NULL
        OR tenant_id = current_setting('app.current_tenant_id', true)::uuid
    );

-- 所有権
ALTER TABLE app.tenants          OWNER TO kintai_app;
ALTER TABLE app.outbox_events    OWNER TO kintai_app;
ALTER TABLE app.processed_events OWNER TO kintai_app;
