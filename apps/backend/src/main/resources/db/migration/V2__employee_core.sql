-- V2__employee_core.sql
-- 社員情報・契約・銀行口座・扶養家族・通勤経路・緊急連絡先
-- 8 tables: department, position, employee, contract, bank_account,
--           dependent_family, commute_route, emergency_contact

-- 部署 (自己参照のツリー構造)
CREATE TABLE app.department (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (parent_id) REFERENCES app.department(id) ON DELETE RESTRICT
);
CREATE INDEX idx_dept_code ON app.department(code) WHERE deleted_at IS NULL;
CREATE INDEX idx_dept_parent ON app.department(parent_id) WHERE deleted_at IS NULL;

-- 役職
CREATE TABLE app.position (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    rank SMALLINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT ck_position_rank CHECK (rank >= 1 AND rank <= 99)
);
CREATE INDEX idx_position_code ON app.position(code) WHERE deleted_at IS NULL;

-- 社員
CREATE TABLE app.employee (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    full_name_kana VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    postal_code VARCHAR(8),
    birth_date DATE,
    hire_date DATE NOT NULL,
    department_id BIGINT NOT NULL,
    position_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE','ON_LEAVE','RETIRED','SUSPENDED')),
    mynumber_encrypted BYTEA,
    mynumber_hash BYTEA,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (department_id) REFERENCES app.department(id) ON DELETE RESTRICT,
    FOREIGN KEY (position_id) REFERENCES app.position(id) ON DELETE RESTRICT
);
CREATE INDEX idx_emp_code ON app.employee(code) WHERE deleted_at IS NULL;
CREATE INDEX idx_emp_dept ON app.employee(department_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_emp_position ON app.employee(position_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_emp_status ON app.employee(status) WHERE deleted_at IS NULL;
-- マイナンバーは UNIQUE 検索用にハッシュで
CREATE UNIQUE INDEX idx_emp_mynumber_hash ON app.employee(mynumber_hash) WHERE deleted_at IS NULL AND mynumber_hash IS NOT NULL;

-- 労働契約
CREATE TABLE app.contract (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    contract_type VARCHAR(20) NOT NULL
        CHECK (contract_type IN ('正社員','契約社員','派遣社員','アルバイト','パート')),
    start_date DATE NOT NULL,
    end_date DATE,
    base_salary NUMERIC(12,2) NOT NULL,
    payment_type VARCHAR(20) NOT NULL DEFAULT 'MONTHLY'
        CHECK (payment_type IN ('MONTHLY','HOURLY','DAILY')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT,
    CONSTRAINT ck_contract_dates CHECK (end_date IS NULL OR end_date >= start_date)
);
CREATE INDEX idx_contract_emp ON app.contract(employee_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_contract_dates ON app.contract(start_date, end_date) WHERE deleted_at IS NULL;

-- 銀行口座 (口座番号は KMS 暗号化)
CREATE TABLE app.bank_account (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    bank_code VARCHAR(4) NOT NULL,
    branch_code VARCHAR(3) NOT NULL,
    account_type VARCHAR(10) NOT NULL DEFAULT 'ORDINARY'
        CHECK (account_type IN ('ORDINARY','SAVINGS','CURRENT')),
    account_number_encrypted BYTEA NOT NULL,
    account_number_hash BYTEA NOT NULL,
    account_holder VARCHAR(100) NOT NULL,
    account_holder_kana VARCHAR(100),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT
);
CREATE INDEX idx_bank_emp ON app.bank_account(employee_id) WHERE deleted_at IS NULL;
-- 社員ごとに代表口座は1つのみ
CREATE UNIQUE INDEX idx_bank_primary ON app.bank_account(employee_id) WHERE is_primary = TRUE AND deleted_at IS NULL;

-- 扶養家族 (社会保険・所得税に影響)
CREATE TABLE app.dependent_family (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    full_name_kana VARCHAR(100),
    relationship VARCHAR(20) NOT NULL
        CHECK (relationship IN ('配偶者','子','親','祖父母','兄弟姉妹','その他')),
    birth_date DATE,
    mynumber_encrypted BYTEA,
    social_insurance_status VARCHAR(20) NOT NULL DEFAULT 'NONE'
        CHECK (social_insurance_status IN ('NONE','ENROLLED','EXEMPTED')),
    income NUMERIC(12,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT
);
CREATE INDEX idx_dep_emp ON app.dependent_family(employee_id) WHERE deleted_at IS NULL;

-- 通勤経路 (通勤手当の計算根拠)
CREATE TABLE app.commute_route (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    from_station VARCHAR(100) NOT NULL,
    to_station VARCHAR(100) NOT NULL,
    via_stations TEXT[],
    distance_km NUMERIC(6,1) NOT NULL,
    monthly_pass_cost NUMERIC(10,2) NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT,
    CONSTRAINT ck_commute_cost CHECK (monthly_pass_cost >= 0 AND monthly_pass_cost <= 100000)
);
CREATE INDEX idx_commute_emp ON app.commute_route(employee_id) WHERE deleted_at IS NULL;

-- 緊急連絡先
CREATE TABLE app.emergency_contact (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    relationship VARCHAR(20) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address TEXT,
    priority SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT
);
CREATE INDEX idx_emergency_emp ON app.emergency_contact(employee_id) WHERE deleted_at IS NULL;

-- シードデータ: 部署
INSERT INTO app.department (code, name, parent_id, created_by, updated_by) VALUES
  ('D001', '経営企画部', NULL, 'system', 'system'),
  ('D002', '営業部',     NULL, 'system', 'system'),
  ('D003', '開発部',     NULL, 'system', 'system'),
  ('D004', '人事部',     NULL, 'system', 'system'),
  ('D005', '総務部',     NULL, 'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- シードデータ: 役職
INSERT INTO app.position (code, name, rank, created_by, updated_by) VALUES
  ('P001', '社長',     1, 'system', 'system'),
  ('P002', '副社長',   2, 'system', 'system'),
  ('P003', '部長',     3, 'system', 'system'),
  ('P004', '課長',     4, 'system', 'system'),
  ('P005', '係長',     5, 'system', 'system'),
  ('P006', '主任',     6, 'system', 'system'),
  ('P007', '一般',     7, 'system', 'system'),
  ('P008', 'パート',   8, 'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- 所有権
ALTER TABLE app.department        OWNER TO kintai_app;
ALTER TABLE app.position          OWNER TO kintai_app;
ALTER TABLE app.employee          OWNER TO kintai_app;
ALTER TABLE app.contract          OWNER TO kintai_app;
ALTER TABLE app.bank_account      OWNER TO kintai_app;
ALTER TABLE app.dependent_family  OWNER TO kintai_app;
ALTER TABLE app.commute_route     OWNER TO kintai_app;
ALTER TABLE app.emergency_contact OWNER TO kintai_app;
