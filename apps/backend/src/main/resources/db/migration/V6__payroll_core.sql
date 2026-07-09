-- V6__payroll_core.sql
-- 給与計算: payroll_run, payroll_item (partitioned)
-- F1 FIX: payroll immutability per 労基法 §108 (prevent historical UPDATE/DELETE)
-- F2 FIX: commute_allowance INCLUDED in gross_pay generated column

CREATE TABLE app.payroll_run (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    pay_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT'
        CHECK (status IN ('DRAFT','PROCESSING','PUBLISHED','PAID')),
    total_gross NUMERIC(14,2) NOT NULL DEFAULT 0,
    total_net NUMERIC(14,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT ck_pr_period CHECK (period_end >= period_start),
    CONSTRAINT ck_pr_paydate CHECK (pay_date >= period_start),
    UNIQUE (period_start, period_end)
);
CREATE INDEX idx_pr_status ON app.payroll_run(status) WHERE deleted_at IS NULL;

CREATE TABLE app.payroll_item (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    payroll_run_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    pay_period_start DATE NOT NULL,
    pay_period_end DATE NOT NULL,
    base_salary NUMERIC(12,2) NOT NULL DEFAULT 0,
    overtime_pay NUMERIC(12,2) NOT NULL DEFAULT 0,
    bonus_pay NUMERIC(12,2) NOT NULL DEFAULT 0,
    commute_allowance NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_allowances NUMERIC(12,2) NOT NULL DEFAULT 0,
    -- F2 FIX: commute_allowance INCLUDED (労基法 §108 immutability, single source of truth)
    gross_pay NUMERIC(12,2) GENERATED ALWAYS AS
        (base_salary + overtime_pay + bonus_pay + commute_allowance + total_allowances) STORED,
    total_deductions NUMERIC(12,2) NOT NULL DEFAULT 0,
    net_pay NUMERIC(12,2) GENERATED ALWAYS AS (gross_pay - total_deductions) STORED,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    PRIMARY KEY (id, pay_period_start),
    FOREIGN KEY (payroll_run_id) REFERENCES app.payroll_run(id) ON DELETE RESTRICT,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT,
    CONSTRAINT ck_pi_period CHECK (pay_period_end >= pay_period_start)
) PARTITION BY RANGE (pay_period_start);

CREATE INDEX idx_pi_emp ON app.payroll_item(employee_id, pay_period_start DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_pi_run ON app.payroll_item(payroll_run_id) WHERE deleted_at IS NULL;

ALTER TABLE app.payroll_run  ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.payroll_run  FORCE  ROW LEVEL SECURITY;
ALTER TABLE app.payroll_item ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.payroll_item FORCE  ROW LEVEL SECURITY;

-- F1 FIX: 労基法 §108 payroll immutability
-- 過去月の給与データは変更・削除禁止
CREATE OR REPLACE FUNCTION app.prevent_historical_payroll_modify() RETURNS trigger
LANGUAGE plpgsql AS $$
DECLARE
    v_current_month DATE := date_trunc('month', now())::date;
BEGIN
    IF TG_OP = 'UPDATE' THEN
        -- 当月のみ修正可、過去月は完全immutable
        IF OLD.pay_period_start < v_current_month THEN
            RAISE EXCEPTION 'Cannot UPDATE historical payroll_item (period < current month, 労基法 §108 immutability)';
        END IF;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        IF OLD.pay_period_start < v_current_month THEN
            RAISE EXCEPTION 'Cannot DELETE historical payroll_item (period < current month, 労基法 §108 immutability)';
        END IF;
        RETURN OLD;
    END IF;
    RETURN COALESCE(NEW, OLD);
END
$$;

CREATE TRIGGER tg_payroll_item_immutable
    BEFORE UPDATE OR DELETE ON app.payroll_item
    FOR EACH ROW EXECUTE FUNCTION app.prevent_historical_payroll_modify();

-- 同様に payroll_run も過去月はimmutable
CREATE OR REPLACE FUNCTION app.prevent_historical_payroll_run_modify() RETURNS trigger
LANGUAGE plpgsql AS $$
DECLARE
    v_current_month DATE := date_trunc('month', now())::date;
BEGIN
    IF TG_OP = 'UPDATE' THEN
        IF OLD.status IN ('PUBLISHED', 'PAID') AND OLD.period_start < v_current_month THEN
            RAISE EXCEPTION 'Cannot UPDATE historical PUBLISHED/PAID payroll_run (period < current month, 労基法 §108)';
        END IF;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        IF OLD.status IN ('PUBLISHED', 'PAID') AND OLD.period_start < v_current_month THEN
            RAISE EXCEPTION 'Cannot DELETE historical PUBLISHED/PAID payroll_run (period < current month, 労基法 §108)';
        END IF;
        RETURN OLD;
    END IF;
    RETURN COALESCE(NEW, OLD);
END
$$;

CREATE TRIGGER tg_payroll_run_immutable
    BEFORE UPDATE OR DELETE ON app.payroll_run
    FOR EACH ROW EXECUTE FUNCTION app.prevent_historical_payroll_run_modify();

ALTER TABLE app.payroll_run  OWNER TO kintai_app;
ALTER TABLE app.payroll_item OWNER TO kintai_app;