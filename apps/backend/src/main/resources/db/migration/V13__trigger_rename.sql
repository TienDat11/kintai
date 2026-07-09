-- V13__trigger_rename.sql
-- トリガー名の名前空間整理 (F7 FIX: 衝突回避)

-- 監査トリガーを audit_t_<table> 形式にリネーム
DO $$
DECLARE
    r RECORD;
    v_new TEXT;
    v_count INT := 0;
BEGIN
    FOR r IN
        SELECT t.tgname, c.relname AS table_name
        FROM pg_trigger t
        JOIN pg_class c ON t.tgrelid = c.oid
        JOIN pg_namespace n ON c.relnamespace = n.oid
        WHERE n.nspname = 'app'
          AND t.tgname LIKE 'tg_audit_%'
          AND NOT t.tgname LIKE 'tg_audit_v%'
          AND NOT t.tgname ~ '^audit_t_'
    LOOP
        v_new := 'audit_t_' || substring(r.tgname FROM 9);  -- 'tg_audit_' を除去
        BEGIN
            EXECUTE format('ALTER TRIGGER %I ON app.%I RENAME TO %I',
                          r.tgname, r.table_name, v_new);
            v_count := v_count + 1;
        EXCEPTION WHEN OTHERS THEN
            RAISE NOTICE 'rename skipped for %.% : %', r.table_name, r.tgname, SQLERRM;
        END;
    END LOOP;
    RAISE NOTICE 'Renamed % audit triggers to audit_t_* namespace', v_count;
END
$$;

-- 給与不変トリガー (V6 で作成) もリネーム
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'tg_payroll_item_immutable') THEN
        ALTER TRIGGER tg_payroll_item_immutable ON app.payroll_item RENAME TO audit_t_payroll_item_immutable;
    END IF;
    IF EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'tg_payroll_run_immutable') THEN
        ALTER TRIGGER tg_payroll_run_immutable ON app.payroll_run RENAME TO audit_t_payroll_run_immutable;
    END IF;
END
$$;