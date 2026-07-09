-- V0__init_schemas_roles.sql
-- スキーマ + ロール作成 (F-B FIX: idempotent)

-- スキーマ作成
CREATE SCHEMA IF NOT EXISTS app;
CREATE SCHEMA IF NOT EXISTS audit;
CREATE SCHEMA IF NOT EXISTS archive;

-- ロール作成 (DO block で冪等性確保)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname='kintai_app') THEN
    CREATE ROLE kintai_app NOLOGIN;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname='kintai_login') THEN
    CREATE ROLE kintai_login LOGIN;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname='audit_writer') THEN
    CREATE ROLE audit_writer NOLOGIN BYPASSRLS;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname='audit_reader') THEN
    CREATE ROLE audit_reader NOLOGIN;
  END IF;
END
$$;

-- スキーマ使用権
GRANT USAGE ON SCHEMA app, audit, archive TO kintai_app, kintai_login, audit_writer, audit_reader;

-- login role は app role を継承
GRANT kintai_app TO kintai_login;
GRANT audit_writer TO kintai_login;
GRANT audit_reader TO kintai_login;

-- デフォルト権限（将来のテーブル用）
ALTER DEFAULT PRIVILEGES IN SCHEMA app
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO kintai_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA app
  GRANT USAGE, SELECT ON SEQUENCES TO kintai_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA audit
  GRANT SELECT, INSERT ON TABLES TO audit_writer;
ALTER DEFAULT PRIVILEGES IN SCHEMA audit
  GRANT SELECT ON TABLES TO audit_reader;
ALTER DEFAULT PRIVILEGES IN SCHEMA audit
  GRANT USAGE, SELECT ON SEQUENCES TO audit_writer;
ALTER DEFAULT PRIVILEGES IN SCHEMA audit
  GRANT SELECT ON SEQUENCES TO audit_reader;