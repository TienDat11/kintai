-- V1__rbac_core.sql
-- RBAC core: role, permission, role_permission

CREATE TABLE app.role (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_role_code ON app.role(code) WHERE deleted_at IS NULL;

CREATE TABLE app.permission (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_perm_code ON app.permission(code) WHERE deleted_at IS NULL;

CREATE TABLE app.role_permission (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    granted_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    granted_by VARCHAR(50) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES app.role(id) ON DELETE RESTRICT,
    FOREIGN KEY (permission_id) REFERENCES app.permission(id) ON DELETE RESTRICT
);
CREATE INDEX idx_rp_perm ON app.role_permission(permission_id);

-- シードデータ: 6 roles
INSERT INTO app.role (code, name, description, created_by, updated_by) VALUES
  ('ADMIN',           'システム管理者',   '全権限',                       'system', 'system'),
  ('HR_MANAGER',      '人事マネージャー', '社員管理・承認',              'system', 'system'),
  ('PAYROLL_OFFICER', '給与担当者',       '給与計算・PDF発行',           'system', 'system'),
  ('MANAGER',         '部門マネージャー', '承認・チーム管理',            'system', 'system'),
  ('EMPLOYEE',        '一般社員',         '自分の勤怠・有給参照',        'system', 'system'),
  ('AUDITOR',         '監査者',           '監査ログ参照',                'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- シードデータ: 8 permissions
INSERT INTO app.permission (code, description, created_by, updated_by) VALUES
  ('employee:read',    '社員情報参照',    'system', 'system'),
  ('employee:write',   '社員情報更新',    'system', 'system'),
  ('attendance:write', '勤怠打刻',        'system', 'system'),
  ('leave:approve',    '休暇承認',        'system', 'system'),
  ('payroll:read',     '給与情報参照',    'system', 'system'),
  ('payroll:write',    '給与計算実行',    'system', 'system'),
  ('audit:read',       '監査ログ参照',    'system', 'system'),
  ('admin:all',        '管理者権限',      'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- ロール別権限マッピング
INSERT INTO app.role_permission (role_id, permission_id, granted_by)
SELECT r.id, p.id, 'system' FROM app.role r, app.permission p
WHERE
  (r.code = 'ADMIN' AND 1=1) OR
  (r.code = 'HR_MANAGER' AND p.code IN ('employee:read','employee:write','attendance:write','leave:approve','payroll:read')) OR
  (r.code = 'PAYROLL_OFFICER' AND p.code IN ('employee:read','payroll:read','payroll:write')) OR
  (r.code = 'MANAGER' AND p.code IN ('employee:read','attendance:write','leave:approve')) OR
  (r.code = 'EMPLOYEE' AND p.code IN ('attendance:write')) OR
  (r.code = 'AUDITOR' AND p.code IN ('audit:read'))
ON CONFLICT DO NOTHING;

-- 所有権
ALTER TABLE app.role            OWNER TO kintai_app;
ALTER TABLE app.permission      OWNER TO kintai_app;
ALTER TABLE app.role_permission OWNER TO kintai_app;