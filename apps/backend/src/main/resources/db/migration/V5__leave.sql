-- V5__leave.sql
-- 休暇管理: leave_type, leave_balance, leave_request

CREATE TABLE app.leave_type (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    days_per_year NUMERIC(5,1),
    is_paid BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_leave_type_code ON app.leave_type(code) WHERE deleted_at IS NULL;

CREATE TABLE app.leave_balance (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    fiscal_year INT NOT NULL,
    leave_type_id BIGINT NOT NULL,
    granted_days NUMERIC(5,1) NOT NULL DEFAULT 0,
    used_days NUMERIC(5,1) NOT NULL DEFAULT 0,
    remaining_days NUMERIC(5,1) GENERATED ALWAYS AS (granted_days - used_days) STORED,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT,
    FOREIGN KEY (leave_type_id) REFERENCES app.leave_type(id) ON DELETE RESTRICT,
    UNIQUE (employee_id, fiscal_year, leave_type_id),
    CONSTRAINT ck_balance_used CHECK (used_days >= 0 AND used_days <= granted_days)
);
CREATE INDEX idx_lb_emp ON app.leave_balance(employee_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_lb_year ON app.leave_balance(fiscal_year) WHERE deleted_at IS NULL;

CREATE TABLE app.leave_request (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    leave_type_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days NUMERIC(5,1) NOT NULL,
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING','APPROVED','REJECTED','CANCELLED')),
    approver_id BIGINT,
    approved_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT,
    FOREIGN KEY (leave_type_id) REFERENCES app.leave_type(id) ON DELETE RESTRICT,
    FOREIGN KEY (approver_id) REFERENCES app.employee(id) ON DELETE RESTRICT,
    CONSTRAINT ck_leave_dates CHECK (end_date >= start_date)
);
CREATE INDEX idx_lr_emp ON app.leave_request(employee_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_lr_status ON app.leave_request(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_lr_approver ON app.leave_request(approver_id) WHERE deleted_at IS NULL AND status = 'PENDING';

ALTER TABLE app.leave_type     ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.leave_type     FORCE  ROW LEVEL SECURITY;
ALTER TABLE app.leave_balance  ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.leave_balance  FORCE  ROW LEVEL SECURITY;
ALTER TABLE app.leave_request  ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.leave_request  FORCE  ROW LEVEL SECURITY;

-- シードデータ: 6 leave types
INSERT INTO app.leave_type (code, name, days_per_year, is_paid, created_by, updated_by) VALUES
  ('PAID',       '有給休暇',     20.0, TRUE,  'system', 'system'),
  ('SICK',       '病欠',         NULL, FALSE, 'system', 'system'),
  ('MATERNITY',  '産前産後休暇', NULL, TRUE,  'system', 'system'),
  ('CHILDCARE',  '育児休暇',     NULL, TRUE,  'system', 'system'),
  ('BEREAVEMENT','忌引',         NULL, TRUE,  'system', 'system'),
  ('SUBSTITUTE', '振替休日',     NULL, TRUE,  'system', 'system')
ON CONFLICT (code) DO NOTHING;

ALTER TABLE app.leave_type     OWNER TO kintai_app;
ALTER TABLE app.leave_balance  OWNER TO kintai_app;
ALTER TABLE app.leave_request  OWNER TO kintai_app;