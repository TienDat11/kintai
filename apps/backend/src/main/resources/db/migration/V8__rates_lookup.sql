-- V8__rates_lookup.sql
-- 税率・保険率ルックアップ (2026年度版シード)

-- 所得税率 (国税)
CREATE TABLE app.tax_rate (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fiscal_year INT NOT NULL,
    bracket_min NUMERIC(12,0) NOT NULL,
    bracket_max NUMERIC(12,0),
    rate NUMERIC(5,2) NOT NULL,
    deduction NUMERIC(12,0) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    UNIQUE (fiscal_year, bracket_min),
    CONSTRAINT ck_tax_bracket CHECK (bracket_max IS NULL OR bracket_max > bracket_min),
    CONSTRAINT ck_tax_rate CHECK (rate >= 0 AND rate <= 100)
);
CREATE INDEX idx_tax_year ON app.tax_rate(fiscal_year) WHERE deleted_at IS NULL;

-- 社会保険率
CREATE TABLE app.social_insurance_rate (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fiscal_year INT NOT NULL,
    insurance_type VARCHAR(20) NOT NULL
        CHECK (insurance_type IN ('健康保険','厚生年金','雇用保険','介護保険','労災保険')),
    rate NUMERIC(5,2) NOT NULL,
    employee_rate NUMERIC(5,2) NOT NULL,
    employer_rate NUMERIC(5,2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    UNIQUE (fiscal_year, insurance_type),
    CONSTRAINT ck_si_rate CHECK (rate >= 0 AND rate <= 100)
);
CREATE INDEX idx_si_year ON app.social_insurance_rate(fiscal_year) WHERE deleted_at IS NULL;

-- 2026年度 所得税率 (7段階、復興特別所得税 2.1% は別途)
INSERT INTO app.tax_rate (fiscal_year, bracket_min, bracket_max, rate, deduction, created_by, updated_by) VALUES
  (2026,       0,    1950000,  5.00,       0, 'system', 'system'),
  (2026,  1950000,    3300000, 10.00,   97500, 'system', 'system'),
  (2026,  3300000,    6950000, 20.00,  427500, 'system', 'system'),
  (2026,  6950000,    9000000, 23.00,  636000, 'system', 'system'),
  (2026,  9000000,   18000000, 33.00, 1536000, 'system', 'system'),
  (2026, 18000000,   40000000, 40.00, 2796000, 'system', 'system'),
  (2026, 40000000,        NULL, 45.00, 4796000, 'system', 'system')
ON CONFLICT (fiscal_year, bracket_min) DO NOTHING;

-- 2026年度 社会保険率
INSERT INTO app.social_insurance_rate (fiscal_year, insurance_type, rate, employee_rate, employer_rate, created_by, updated_by) VALUES
  (2026, '健康保険',   9.50, 4.75, 4.75, 'system', 'system'),
  (2026, '厚生年金',  18.30, 9.15, 9.15, 'system', 'system'),
  (2026, '雇用保険',   0.60, 0.30, 0.30, 'system', 'system'),
  (2026, '介護保険',   1.80, 0.90, 0.90, 'system', 'system'),
  (2026, '労災保険',   0.30, 0.00, 0.30, 'system', 'system')
ON CONFLICT (fiscal_year, insurance_type) DO NOTHING;

ALTER TABLE app.tax_rate               OWNER TO kintai_app;
ALTER TABLE app.social_insurance_rate  OWNER TO kintai_app;