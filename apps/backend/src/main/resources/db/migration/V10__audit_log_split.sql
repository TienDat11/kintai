-- V10__audit_log_split.sql
-- 監査ログ (F-A FIX: compact + detail split, Neon 0.5GB cap 内に収める)
-- コンパクト行: 毎mutations、JSONBなし (73MB at 5yr)
-- 詳細行: PII テーブルのみ JSONB (290MB at 5yr)

-- コンパクト監査ログ (全テーブル対象)
CREATE TABLE app.audit_log (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    table_name TEXT NOT NULL,
    record_id BIGINT NOT NULL,
    operation CHAR(1) NOT NULL CHECK (operation IN ('I','U','D')),
    -- Note: TRUNCATE is a DDL operation that bypasses row triggers.
    -- To audit TRUNCATE, add STATEMENT-level trigger per source table (not implemented for MVP).
    changed_by TEXT,
    client_ip INET,
    PRIMARY KEY (id, occurred_at)
) PARTITION BY RANGE (occurred_at);

CREATE INDEX idx_al_table_record ON app.audit_log(table_name, record_id);
CREATE INDEX idx_al_changed_by ON app.audit_log(changed_by, occurred_at DESC);
CREATE INDEX idx_al_occurred ON app.audit_log(occurred_at DESC);

ALTER TABLE app.audit_log ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.audit_log FORCE  ROW LEVEL SECURITY;

-- 詳細監査ログ (PII テーブルのみ)
CREATE TABLE app.audit_log_detail (
    audit_id BIGINT NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    old_data JSONB,
    new_data JSONB,
    PRIMARY KEY (audit_id, occurred_at),
    FOREIGN KEY (audit_id, occurred_at) REFERENCES app.audit_log(id, occurred_at) ON DELETE CASCADE
) PARTITION BY RANGE (occurred_at);

CREATE INDEX idx_ald_occurred ON app.audit_log_detail(occurred_at DESC);
CREATE INDEX idx_ald_old_data ON app.audit_log_detail USING GIN (old_data);
CREATE INDEX idx_ald_new_data ON app.audit_log_detail USING GIN (new_data);

ALTER TABLE app.audit_log_detail ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.audit_log_detail FORCE  ROW LEVEL SECURITY;

-- 監査トリガー関数: PII テーブルのみ詳細を記録
CREATE OR REPLACE FUNCTION app.tg_audit() RETURNS trigger
LANGUAGE plpgsql AS $$
DECLARE
    v_id BIGINT;
    v_when TIMESTAMPTZ := now();
    v_pii BOOLEAN := TG_TABLE_NAME IN ('employee','payroll_item','bank_account','dependent_family','contract','payroll_run','year_end_adjustment','bonus_payment');
BEGIN
    IF TG_OP IN ('INSERT','UPDATE','DELETE') THEN
        BEGIN
            INSERT INTO app.audit_log(occurred_at, table_name, record_id, operation, changed_by, client_ip)
            VALUES (v_when, TG_TABLE_NAME,
                    COALESCE((NEW.id)::bigint, (OLD.id)::bigint),
                    CASE TG_OP WHEN 'INSERT' THEN 'I' WHEN 'UPDATE' THEN 'U' ELSE 'D' END,
                    current_setting('app.user_id', true),
                    inet_client_addr())
            RETURNING id INTO v_id;
        EXCEPTION WHEN OTHERS THEN
            -- Partition missing for current month (edge case on month boundary)
            RAISE WARNING 'audit_log insert failed for %.%: %', TG_TABLE_NAME, TG_OP, SQLERRM;
            RETURN COALESCE(NEW, OLD);
        END;

        -- PII テーブルは詳細も保存
        IF v_pii AND TG_OP = 'UPDATE' THEN
            INSERT INTO app.audit_log_detail(audit_id, occurred_at, old_data, new_data)
            VALUES (v_id, v_when, to_jsonb(OLD), to_jsonb(NEW));
        END IF;
    END IF;
    RETURN COALESCE(NEW, OLD);
END
$$;

-- 24 業務テーブルにトリガー付与
DO $$
DECLARE
    t TEXT;
    tables TEXT[] := ARRAY[
      'department','position','employee','contract','bank_account',
      'dependent_family','commute_route','emergency_contact',
      'role','permission','role_permission','employee_role',
      'shift','shift_assignment','attendance_record','break_record',
      'leave_type','leave_balance','leave_request',
      'payroll_run','payroll_item','allowance','deduction','bonus_payment',
      'year_end_adjustment','resident_tax','notification','file_attachment',
      'approval_workflow','project','refresh_token','timesheet','timesheet_entry',
      'company_setting','holiday'
    ];
BEGIN
    FOREACH t IN ARRAY tables LOOP
        EXECUTE format(
            'CREATE TRIGGER tg_audit_%I AFTER INSERT OR UPDATE OR DELETE ON app.%I
             FOR EACH ROW EXECUTE FUNCTION app.tg_audit()', t, t);
    END LOOP;
END
$$;

-- 所有権 (audit_writer BYPASSRLS なので owner は kintai_app でもOK)
ALTER TABLE app.audit_log        OWNER TO kintai_app;
ALTER TABLE app.audit_log_detail OWNER TO kintai_app;

-- GRANT (audit_writer は INSERT+SELECT、app は SELECT のみ)
GRANT INSERT, SELECT ON app.audit_log TO audit_writer;
GRANT INSERT, SELECT ON app.audit_log_detail TO audit_writer;
GRANT SELECT ON app.audit_log TO audit_reader;
GRANT SELECT ON app.audit_log_detail TO audit_reader;
GRANT SELECT ON app.audit_log TO kintai_app;
GRANT SELECT ON app.audit_log_detail TO kintai_app;