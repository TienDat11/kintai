-- V9__supporting.sql
-- 補助テーブル: notification, file_attachment, refresh_token, approval_workflow,
--               timesheet, project, timesheet_entry, company_setting, holiday

-- 通知
CREATE TABLE app.notification (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    body TEXT,
    link_url VARCHAR(500),
    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    FOREIGN KEY (recipient_id) REFERENCES app.employee(id) ON DELETE RESTRICT
);
CREATE INDEX idx_notif_recipient ON app.notification(recipient_id, created_at DESC);
CREATE INDEX idx_notif_unread ON app.notification(recipient_id) WHERE read_at IS NULL;

-- ファイル添付 (Cloudflare R2 への path を保存)
CREATE TABLE app.file_attachment (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    uploaded_by BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    FOREIGN KEY (uploaded_by) REFERENCES app.employee(id) ON DELETE RESTRICT
);
CREATE INDEX idx_file_entity ON app.file_attachment(entity_type, entity_id);

-- リフレッシュトークン (Keycloak ではなく、独自 JWT 認証の場合)
CREATE TABLE app.refresh_token (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    FOREIGN KEY (user_id) REFERENCES app.employee(id) ON DELETE RESTRICT
);
CREATE INDEX idx_rt_user ON app.refresh_token(user_id) WHERE revoked_at IS NULL;

-- 承認ワークフロー
CREATE TABLE app.approval_workflow (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    current_step INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING','APPROVED','REJECTED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_aw_entity ON app.approval_workflow(entity_type, entity_id);
CREATE INDEX idx_aw_pending ON app.approval_workflow(status) WHERE status = 'PENDING';

-- プロジェクト (工数管理)
CREATE TABLE app.project (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    start_date DATE,
    end_date DATE,
    budget NUMERIC(14,2),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE','CLOSED','CANCELLED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT ck_project_dates CHECK (end_date IS NULL OR end_date >= start_date)
);
CREATE INDEX idx_project_code ON app.project(code) WHERE deleted_at IS NULL;

-- タイムシート (月次)
CREATE TABLE app.timesheet (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    fiscal_year INT NOT NULL,
    fiscal_month INT NOT NULL CHECK (fiscal_month BETWEEN 1 AND 12),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT'
        CHECK (status IN ('DRAFT','SUBMITTED','APPROVED','REJECTED')),
    approved_by BIGINT,
    approved_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT,
    FOREIGN KEY (approved_by) REFERENCES app.employee(id) ON DELETE RESTRICT,
    UNIQUE (employee_id, fiscal_year, fiscal_month)
);
CREATE INDEX idx_ts_emp ON app.timesheet(employee_id) WHERE deleted_at IS NULL;

-- タイムシート明細
CREATE TABLE app.timesheet_entry (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    timesheet_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    hours NUMERIC(4,2) NOT NULL CHECK (hours >= 0 AND hours <= 24),
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (timesheet_id) REFERENCES app.timesheet(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES app.project(id) ON DELETE RESTRICT
);
CREATE INDEX idx_tse_timesheet ON app.timesheet_entry(timesheet_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_tse_project_date ON app.timesheet_entry(project_id, work_date) WHERE deleted_at IS NULL;

-- 会社設定 (key-value)
CREATE TABLE app.company_setting (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    key VARCHAR(100) NOT NULL UNIQUE,
    value TEXT,
    description TEXT,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by VARCHAR(50) NOT NULL
);
CREATE INDEX idx_cs_key ON app.company_setting(key);

-- 祝日
CREATE TABLE app.holiday (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    holiday_date DATE NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    is_substitute BOOLEAN NOT NULL DEFAULT FALSE,
    fiscal_year INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_holiday_year ON app.holiday(fiscal_year) WHERE deleted_at IS NULL;

ALTER TABLE app.notification       OWNER TO kintai_app;
ALTER TABLE app.file_attachment    OWNER TO kintai_app;
ALTER TABLE app.refresh_token      OWNER TO kintai_app;
ALTER TABLE app.approval_workflow  OWNER TO kintai_app;
ALTER TABLE app.project            OWNER TO kintai_app;
ALTER TABLE app.timesheet          OWNER TO kintai_app;
ALTER TABLE app.timesheet_entry    OWNER TO kintai_app;
ALTER TABLE app.company_setting    OWNER TO kintai_app;
ALTER TABLE app.holiday            OWNER TO kintai_app;