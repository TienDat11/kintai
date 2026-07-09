-- V3__employee_relations.sql
-- employee_role 社員-ロールマッピング
-- RLS FORCE on RBAC + employee tables

CREATE TABLE app.employee_role (
    employee_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    assigned_by VARCHAR(50) NOT NULL,
    PRIMARY KEY (employee_id, role_id),
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT,
    FOREIGN KEY (role_id) REFERENCES app.role(id) ON DELETE RESTRICT
);
CREATE INDEX idx_emp_role_role ON app.employee_role(role_id);

-- RLS設定 (F6 FIX: FORCE RLS, owner bypass blocked)
ALTER TABLE app.employee       ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.employee       FORCE  ROW LEVEL SECURITY;
ALTER TABLE app.employee_role  ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.employee_role  FORCE  ROW LEVEL SECURITY;
ALTER TABLE app.role           ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.role           FORCE  ROW LEVEL SECURITY;
ALTER TABLE app.role_permission ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.role_permission FORCE  ROW LEVEL SECURITY;

-- サンプルポリシー: テナント分離
-- セッション変数 app.current_tenant_id でフィルタ
CREATE POLICY emp_tenant_isolation ON app.employee
  USING (
    NULLIF(current_setting('app.current_tenant_id', true), '') IS NULL
    OR department_id::text = current_setting('app.current_tenant_id', true)
  );

CREATE POLICY emp_role_tenant ON app.employee_role
  USING (
    NULLIF(current_setting('app.current_tenant_id', true), '') IS NULL
    OR EXISTS (
      SELECT 1 FROM app.employee e
      WHERE e.id = employee_role.employee_id
        AND e.department_id::text = current_setting('app.current_tenant_id', true)
    )
  );

-- 所有権
ALTER TABLE app.employee_role OWNER TO kintai_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON app.employee_role TO kintai_app;
