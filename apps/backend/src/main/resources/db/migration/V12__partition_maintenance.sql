-- V12__partition_maintenance.sql
-- パーティション保守 (F3 FIX: RLS は audit_log_detail も含む)
-- pg_partman の代替: 純 PL/pgSQL + GitHub Actions cron

-- 5 つのパーティションテーブルの未来パーティションを保証
CREATE OR REPLACE FUNCTION app.ensure_future_partitions(p_months_ahead INT DEFAULT 3)
RETURNS void LANGUAGE plpgsql AS $$
DECLARE
    v_target DATE := date_trunc('month', now())::date + (p_months_ahead || ' months')::interval;
    v_part   TEXT := 'm' || EXTRACT(YEAR FROM v_target)::text || lpad(EXTRACT(MONTH FROM v_target)::text, 2, '0');
    v_start  DATE := date_trunc('month', v_target)::date;
    v_end    DATE := v_start + interval '1 month';
BEGIN
    -- attendance_record
    EXECUTE format(
        'CREATE TABLE IF NOT EXISTS app.attendance_record_%I PARTITION OF app.attendance_record
         FOR VALUES FROM (%L) TO (%L)', v_part, v_start, v_end);
    -- break_record
    EXECUTE format(
        'CREATE TABLE IF NOT EXISTS app.break_record_%I PARTITION OF app.break_record
         FOR VALUES FROM (%L) TO (%L)', v_part, v_start, v_end);
    -- payroll_item
    EXECUTE format(
        'CREATE TABLE IF NOT EXISTS app.payroll_item_%I PARTITION OF app.payroll_item
         FOR VALUES FROM (%L) TO (%L)', v_part, v_start, v_end);
    -- audit_log
    EXECUTE format(
        'CREATE TABLE IF NOT EXISTS app.audit_log_%I PARTITION OF app.audit_log
         FOR VALUES FROM (%L) TO (%L)', v_part, v_start, v_end);
    -- audit_log_detail
    EXECUTE format(
        'CREATE TABLE IF NOT EXISTS app.audit_log_detail_%I PARTITION OF app.audit_log_detail
         FOR VALUES FROM (%L) TO (%L)', v_part, v_start, v_end);
END
$$;

-- 古いパーティションを archive スキーマに切り離し (DROP しない、5年保持)
CREATE OR REPLACE FUNCTION app.detach_old_partitions(p_keep_months INT DEFAULT 24)
RETURNS INT LANGUAGE plpgsql AS $$
DECLARE
    v_cutoff DATE := date_trunc('month', now()) - (p_keep_months || ' months')::interval;
    r RECORD;
    v_count INT := 0;
    v_year_cutoff INT := EXTRACT(YEAR FROM v_cutoff)::int;
BEGIN
    FOR r IN
        SELECT inhrelid::regclass::text AS part_name,
               inhparent::regclass::text AS parent_name
        FROM pg_inherits
        WHERE inhparent IN ('app.attendance_record'::regclass,
                            'app.break_record'::regclass,
                            'app.payroll_item'::regclass,
                            'app.audit_log'::regclass,
                            'app.audit_log_detail'::regclass)
    LOOP
        -- パーティション名 mYYYYMM から year 抽出
        IF r.part_name ~ '_(m|y)(\d{6}|\d{4})$' THEN
            DECLARE
                v_part_year INT;
            BEGIN
                v_part_year := CASE
                    WHEN r.part_name ~ '_(m|y)\d{6}$' THEN substring(r.part_name FROM '_(m|y)(\d{4})\d{2}$')::int
                    WHEN r.part_name ~ '_(m|y)\d{4}$' THEN substring(r.part_name FROM '_(m|y)(\d{4})$')::int
                    ELSE 9999
                END;

                IF v_part_year < v_year_cutoff THEN
                    BEGIN
                        EXECUTE format('ALTER TABLE %s DETACH PARTITION %s', r.parent_name, r.part_name);
                        EXECUTE format('ALTER TABLE %s SET SCHEMA archive', r.part_name);
                        v_count := v_count + 1;
                    EXCEPTION WHEN OTHERS THEN
                        RAISE NOTICE 'detach skipped for %: %', r.part_name, SQLERRM;
                    END;
                END IF;
            END;
        END IF;
    END LOOP;
    RETURN v_count;
END
$$;

-- F3 FIX: 5 つのパーティションテーブル全てに RLS FORCE (per-partition)
-- PG16 は親から子に RLS を伝播しない
DO $$
DECLARE
    v_parent TEXT;
    v_part   TEXT;
    v_targets TEXT[] := ARRAY[
        'app.attendance_record',
        'app.break_record',
        'app.payroll_item',
        'app.audit_log',
        'app.audit_log_detail'
    ];
BEGIN
    FOREACH v_parent IN ARRAY v_targets LOOP
        FOR v_part IN
            SELECT inhrelid::regclass::text
            FROM pg_inherits
            WHERE inhparent = v_parent::regclass
        LOOP
            EXECUTE format('ALTER TABLE %s ENABLE ROW LEVEL SECURITY', v_part);
            EXECUTE format('ALTER TABLE %s FORCE  ROW LEVEL SECURITY', v_part);
        END LOOP;
    END LOOP;
END
$$;

-- 初期パーティション作成 (今月 + 翌月 + 翌々月)
SELECT app.ensure_future_partitions(0);
SELECT app.ensure_future_partitions(1);
SELECT app.ensure_future_partitions(2);

-- 所有権
ALTER FUNCTION app.ensure_future_partitions(INT) OWNER TO kintai_app;
ALTER FUNCTION app.detach_old_partitions(INT)   OWNER TO kintai_app;