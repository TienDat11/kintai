-- V11__seed_japan_data.sql
-- 日本の祝日を 2026-2030 の 5 年分自動生成 (F2 FIX: shunbun formula corrected)

-- nth_weekday: 第 N 番目の X 曜日 (例: 第2月曜日)
-- p_year: 年, p_month: 月, p_n: 何回目か, p_dow: 曜日 (0=日, 1=月, ..., 6=土)
CREATE OR REPLACE FUNCTION app.nth_weekday(p_year INT, p_month INT, p_n INT, p_dow INT)
RETURNS DATE LANGUAGE sql IMMUTABLE AS $$
    SELECT MIN(d)::date
    FROM generate_series(make_date(p_year, p_month, 1), make_date(p_year, p_month, 31), '1 day'::interval) d
    WHERE EXTRACT(DOW FROM d)::int = p_dow
      AND EXTRACT(DAY FROM d)::int BETWEEN (p_n - 1) * 7 + 1 AND p_n * 7;
$$;

-- F2 FIX: 簡略化春分公式
-- 2026 → 3/20, 2027 → 3/21, 2028 → 3/20, 2029 → 3/20, 2030 → 3/20
CREATE OR REPLACE FUNCTION app.shunbun(p_year INT) RETURNS DATE LANGUAGE sql IMMUTABLE AS $$
    SELECT CASE
        WHEN p_year = 2026 THEN '2026-03-20'::date
        WHEN p_year = 2027 THEN '2027-03-21'::date
        WHEN p_year = 2028 THEN '2028-03-20'::date
        WHEN p_year = 2029 THEN '2029-03-20'::date
        WHEN p_year = 2030 THEN '2030-03-20'::date
        -- フォールバック: 概算 (実際のカレンダーは要更新)
        ELSE make_date(p_year, 3, 20) + (((p_year - 2000)::int * 24219 / 100000) % 2)::int
    END;
$$;

-- 秋分日: 2026 → 9/23, 2027 → 9/23, 2028 → 9/22, 2029 → 9/23, 2030 → 9/23
CREATE OR REPLACE FUNCTION app.shuubun(p_year INT) RETURNS DATE LANGUAGE sql IMMUTABLE AS $$
    SELECT CASE
        WHEN p_year = 2026 THEN '2026-09-23'::date
        WHEN p_year = 2027 THEN '2027-09-23'::date
        WHEN p_year = 2028 THEN '2028-09-22'::date
        WHEN p_year = 2029 THEN '2029-09-23'::date
        WHEN p_year = 2030 THEN '2030-09-23'::date
        ELSE make_date(p_year, 9, 23) - (((p_year - 2000)::int * 24219 / 100000) % 2)::int
    END;
$$;

-- 5 年分の祝日を生成
DO $$
DECLARE
    y INT;
    hd DATE;
    dow INT;
BEGIN
    FOR y IN 2026..2030 LOOP
        -- 固定日の祝日
        INSERT INTO app.holiday(holiday_date, name, is_substitute, fiscal_year, created_by, updated_by) VALUES
          (make_date(y, 1, 1),            '元日',         FALSE, y, 'system', 'system'),
          (make_date(y, 2, 11),           '建国記念の日', FALSE, y, 'system', 'system'),
          (make_date(y, 2, 23),           '天皇誕生日',   FALSE, y, 'system', 'system'),
          (make_date(y, 4, 29),           '昭和の日',     FALSE, y, 'system', 'system'),
          (make_date(y, 5, 3),            '憲法記念日',   FALSE, y, 'system', 'system'),
          (make_date(y, 5, 4),            'みどりの日',   FALSE, y, 'system', 'system'),
          (make_date(y, 5, 5),            'こどもの日',   FALSE, y, 'system', 'system'),
          (make_date(y, 8, 11),           '山の日',       FALSE, y, 'system', 'system'),
          (app.shunbun(y),                '春分の日',     FALSE, y, 'system', 'system'),
          (app.shuubun(y),                '秋分の日',     FALSE, y, 'system', 'system'),
          (make_date(y, 11, 3),           '文化の日',     FALSE, y, 'system', 'system'),
          (make_date(y, 11, 23),          '勤労感謝の日', FALSE, y, 'system', 'system');

        -- ハッピーマンデー制度
        INSERT INTO app.holiday(holiday_date, name, is_substitute, fiscal_year, created_by, updated_by) VALUES
          (app.nth_weekday(y, 1,  2, 1),  '成人の日',     FALSE, y, 'system', 'system'),
          (app.nth_weekday(y, 7,  3, 1),  '海の日',       FALSE, y, 'system', 'system'),
          (app.nth_weekday(y, 9,  3, 1),  '敬老の日',     FALSE, y, 'system', 'system'),
          (app.nth_weekday(y, 10, 2, 1),  'スポーツの日', FALSE, y, 'system', 'system')
        ON CONFLICT (holiday_date) DO NOTHING;

        -- 振替休日: 日曜と重なる祝日の翌月曜日
        FOR hd IN SELECT holiday_date FROM app.holiday
                  WHERE EXTRACT(YEAR FROM holiday_date) = y
                    AND NOT is_substitute
                    AND deleted_at IS NULL LOOP
            dow := EXTRACT(DOW FROM hd);
            IF dow = 0 THEN
                INSERT INTO app.holiday(holiday_date, name, is_substitute, fiscal_year, created_by, updated_by)
                VALUES (hd + 1, '振替休日', TRUE, y, 'system', 'system')
                ON CONFLICT (holiday_date) DO NOTHING;
            END IF;
        END LOOP;
    END LOOP;
END
$$;