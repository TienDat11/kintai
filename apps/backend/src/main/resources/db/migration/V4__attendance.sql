-- V4__attendance.sql
-- 勤怠管理: shift, shift_assignment, attendance_record (partitioned), break_record (partitioned)
-- F5 FIX: ck_break_window enforces 労基法 §66 4h max break
-- F-E FIX: break_record now partitioned by work_date to match attendance_record

-- シフトマスタ
CREATE TABLE app.shift (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    working_hours NUMERIC(5,2) NOT NULL,
    break_minutes INT NOT NULL DEFAULT 60,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT ck_shift_hours CHECK (working_hours >= 0 AND working_hours <= 24),
    CONSTRAINT ck_shift_break CHECK (break_minutes >= 0 AND break_minutes <= 240)
);
CREATE INDEX idx_shift_code ON app.shift(code) WHERE deleted_at IS NULL;

-- シフト割当
CREATE TABLE app.shift_assignment (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    shift_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT,
    FOREIGN KEY (shift_id) REFERENCES app.shift(id) ON DELETE RESTRICT,
    UNIQUE (employee_id, work_date)
);
CREATE INDEX idx_shift_asg_emp_date ON app.shift_assignment(employee_id, work_date) WHERE deleted_at IS NULL;

-- 勤怠記録 (パーティション: 月次)
CREATE TABLE app.attendance_record (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    employee_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    check_in TIMESTAMPTZ,
    check_out TIMESTAMPTZ,
    status VARCHAR(20) NOT NULL DEFAULT '出勤'
        CHECK (status IN ('出勤','欠勤','遅刻','早退','有給','代休','振休','休業')),
    work_month INT GENERATED ALWAYS AS (EXTRACT(MONTH FROM work_date)::int) STORED,
    work_year INT GENERATED ALWAYS AS (EXTRACT(YEAR FROM work_date)::int) STORED,
    gross_work_min INT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    PRIMARY KEY (id, work_date),
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT
) PARTITION BY RANGE (work_date);

CREATE INDEX idx_att_emp_date ON app.attendance_record(employee_id, work_date DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_att_status ON app.attendance_record(status, work_date) WHERE deleted_at IS NULL;
CREATE INDEX idx_att_year_month ON app.attendance_record(work_year, work_month) WHERE deleted_at IS NULL;

ALTER TABLE app.attendance_record ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.attendance_record FORCE  ROW LEVEL SECURITY;

-- 休憩記録 (F-E FIX: 同じく work_date でパーティション)
CREATE TABLE app.break_record (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    attendance_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    break_start TIMESTAMPTZ NOT NULL,
    break_end TIMESTAMPTZ,
    break_type VARCHAR(20) NOT NULL DEFAULT 'lunch'
        CHECK (break_type IN ('lunch','rest','other')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    PRIMARY KEY (id, work_date),
    -- F5 FIX: 労基法 §66 4h max break
    CONSTRAINT ck_break_window CHECK (
      break_end IS NULL
      OR (break_end > break_start AND EXTRACT(EPOCH FROM (break_end - break_start)) <= 4 * 3600)
    )
) PARTITION BY RANGE (work_date);

CREATE INDEX idx_break_att ON app.break_record(attendance_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_break_date ON app.break_record(work_date) WHERE deleted_at IS NULL;

ALTER TABLE app.break_record ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.break_record FORCE  ROW LEVEL SECURITY;

-- シードデータ: 3 シフトパターン
INSERT INTO app.shift (code, name, start_time, end_time, working_hours, break_minutes, created_by, updated_by) VALUES
  ('S001', '通常勤務', '09:00', '18:00', 8.00, 60, 'system', 'system'),
  ('S002', '早番',     '07:00', '16:00', 8.00, 60, 'system', 'system'),
  ('S003', '夜勤',     '22:00', '07:00', 8.00, 60, 'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- 所有権
ALTER TABLE app.shift            OWNER TO kintai_app;
ALTER TABLE app.shift_assignment OWNER TO kintai_app;
ALTER TABLE app.attendance_record OWNER TO kintai_app;
ALTER TABLE app.break_record      OWNER TO kintai_app;