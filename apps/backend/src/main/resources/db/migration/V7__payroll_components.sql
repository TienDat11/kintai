-- V7__payroll_components.sql
-- 給与明細構成: allowance, deduction, bonus_payment, year_end_adjustment, resident_tax

-- 手当内訳
CREATE TABLE app.allowance (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    payroll_item_id BIGINT NOT NULL,
    pay_period_start DATE NOT NULL,
    employee_id BIGINT NOT NULL,
    allowance_type VARCHAR(20) NOT NULL
        CHECK (allowance_type IN ('住宅','家族','役職','資格','その他')),
    amount NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    -- パーティション親へのFKはパーティションキー込み
    FOREIGN KEY (payroll_item_id, pay_period_start) REFERENCES app.payroll_item(id, pay_period_start) ON DELETE RESTRICT,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT
);
CREATE INDEX idx_allow_pi ON app.allowance(payroll_item_id) WHERE deleted_at IS NULL;

-- 控除内訳
CREATE TABLE app.deduction (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    payroll_item_id BIGINT NOT NULL,
    pay_period_start DATE NOT NULL,
    employee_id BIGINT NOT NULL,
    deduction_type VARCHAR(20) NOT NULL
        CHECK (deduction_type IN ('健康保険','厚生年金','雇用保険','所得税','住民税','財形')),
    amount NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (payroll_item_id, pay_period_start) REFERENCES app.payroll_item(id, pay_period_start) ON DELETE RESTRICT,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT
);
CREATE INDEX idx_deduct_pi ON app.deduction(payroll_item_id) WHERE deleted_at IS NULL;

-- 賞与 (賞与は給与と別税率)
CREATE TABLE app.bonus_payment (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    pay_date DATE NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    bonus_type VARCHAR(20) NOT NULL
        CHECK (bonus_type IN ('夏季','冬季','決算','その他')),
    tax_amount NUMERIC(12,2) NOT NULL,
    net_amount NUMERIC(12,2) GENERATED ALWAYS AS (amount - tax_amount) STORED,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT
);
CREATE INDEX idx_bonus_emp ON app.bonus_payment(employee_id, pay_date) WHERE deleted_at IS NULL;

-- 年末調整
CREATE TABLE app.year_end_adjustment (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    fiscal_year INT NOT NULL,
    total_annual_income NUMERIC(14,2) NOT NULL,
    total_tax_withheld NUMERIC(14,2) NOT NULL,
    calculated_tax NUMERIC(14,2) NOT NULL,
    refund_or_balance NUMERIC(14,2) GENERATED ALWAYS AS (total_tax_withheld - calculated_tax) STORED,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT,
    UNIQUE (employee_id, fiscal_year)
);
CREATE INDEX idx_yea_emp ON app.year_end_adjustment(employee_id) WHERE deleted_at IS NULL;

-- 住民税 (市区町村別)
CREATE TABLE app.resident_tax (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    fiscal_year INT NOT NULL,
    municipality_code VARCHAR(6) NOT NULL,
    annual_amount NUMERIC(14,2) NOT NULL,
    monthly_amount NUMERIC(12,2) GENERATED ALWAYS AS ((annual_amount / 12)::numeric(12,2)) STORED,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT,
    UNIQUE (employee_id, fiscal_year, municipality_code)
);
CREATE INDEX idx_resident_emp ON app.resident_tax(employee_id) WHERE deleted_at IS NULL;

ALTER TABLE app.allowance           ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.allowance           FORCE  ROW LEVEL SECURITY;
ALTER TABLE app.deduction           ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.deduction           FORCE  ROW LEVEL SECURITY;
ALTER TABLE app.bonus_payment       ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.bonus_payment       FORCE  ROW LEVEL SECURITY;
ALTER TABLE app.year_end_adjustment ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.year_end_adjustment FORCE  ROW LEVEL SECURITY;
ALTER TABLE app.resident_tax        ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.resident_tax        FORCE  ROW LEVEL SECURITY;

ALTER TABLE app.allowance           OWNER TO kintai_app;
ALTER TABLE app.deduction           OWNER TO kintai_app;
ALTER TABLE app.bonus_payment       OWNER TO kintai_app;
ALTER TABLE app.year_end_adjustment OWNER TO kintai_app;
ALTER TABLE app.resident_tax        OWNER TO kintai_app;