# PHASE 0 — Bật Flyway + hạ tầng kết nối (không có bảng nghiệp vụ mới)

## Business context (tại sao phase này đứng đầu)

14 file migration (`V0__init_schemas_roles.sql` → `V13__trigger_rename.sql`) đã viết sẵn, nhưng
`pom.xml` **chưa có Flyway** nên chúng **chưa từng chạy** trên Neon. Nếu bỏ qua phase này mà nhảy
thẳng vào viết Entity, bạn sẽ code cho bảng không tồn tại → runtime lỗi `relation does not exist`.
Đây là lỗi rất phổ biến khi dev outsource nhận bàn giao dự án có sẵn migration nhưng chưa verify đã
apply — luôn luôn xác minh state DB thật trước khi tin vào file SQL.

Phase này còn giải quyết 2 vấn đề hạ tầng bắt buộc cho MỌI bảng ở các phase sau:
- **RLS (Row Level Security)**: nhiều bảng có `ENABLE ROW LEVEL SECURITY` + `FORCE ROW LEVEL SECURITY`,
  policy dựa vào `current_setting('app.user_id')`. Nếu không `SET` biến này mỗi transaction, mọi
  SELECT/UPDATE sẽ trả về **0 dòng** (không phải lỗi, mà là "câm lặng" — khó debug nhất).
- **Schema `app`**: mọi bảng nằm trong schema `app`, không phải `public`. MyBatis mapper phải viết
  `FROM app.employee` chứ không phải `FROM employee`.

## Scope — KHÔNG có bảng mới, chỉ hạ tầng

1. `pom.xml`: thêm `org.flywaydb:flyway-core` + `org.flywaydb:flyway-database-postgresql` (Flyway 10+
   tách driver riêng cho Postgres — PHẢI thêm cả 2, thiếu `flyway-database-postgresql` sẽ lỗi
   `Unsupported Database: PostgreSQL 16.x`).
2. `application.yml` (chung): tắt tự-tạo schema bằng Hibernate (project này dùng MyBatis nên không có
   Hibernate DDL, nhưng phải khai báo rõ) + cấu hình Flyway:
   ```yaml
   spring:
     flyway:
       enabled: true
       schemas: app,audit,archive
       default-schema: app
       locations: classpath:db/migration
       baseline-on-migrate: false   # DB đang rỗng, không cần baseline
   ```
3. `application-prod.yml`: giữ nguyên `DB_URL/DB_USER/DB_PASSWORD`, Flyway dùng chung datasource này —
   không cần config riêng.
4. **RLS session variable** — tạo 1 class mới `common/config/TenantConnectionCustomizer.java` (hoặc
   dùng `HikariDataSource.setConnectionInitSql` nếu đơn giản hơn) để mỗi connection khi lấy ra khỏi
   pool đều chạy `SET app.user_id = '<current-user-id>'`. Vì user_id đổi theo từng request (không phải
   hằng số), cách đúng là dùng một `@Around` interceptor trên tầng Service/Mapper qua
   `DataSource` + `ConnectionHolder`, HOẶC đơn giản hơn cho MVP: mỗi Mapper XML tự thêm câu
   `SELECT set_config('app.user_id', #{currentUserId}, true)` trước câu query chính trong cùng 1
   MyBatis statement (dùng `;` nối 2 câu) — chọn cách nào do Opus quyết định ở bước ARCHITECT_PLAN khi
   Haiku báo BLOCKED, KHÔNG để Haiku tự quyết.
   → **Khuyến nghị của Opus (đã quyết, Haiku follow)**: dùng cách thứ 2 (nối câu SQL trong Mapper XML)
   cho Phase 0-1, vì đơn giản, không cần thay đổi kiến trúc Spring; nếu sau này cần transaction-scope
   thật (nhiều Mapper trong 1 transaction) sẽ nâng cấp lên Spring AOP `@Around` ở Phase 8.
5. Xoá `schema.sql` cũ (H2 demo) khỏi `src/main/resources/` — KHÔNG cần nữa vì Flyway quản lý toàn bộ,
   để lại sẽ gây xung đột (Spring Boot tự chạy `schema.sql` trước Flyway nếu tồn tại).

## File cụ thể sẽ đổi/tạo

| File | Hành động |
|---|---|
| `apps/backend/pom.xml` | Thêm 2 dependency Flyway |
| `apps/backend/src/main/resources/application.yml` | Thêm block `spring.flyway` |
| `apps/backend/src/main/resources/schema.sql` | XOÁ |
| `apps/backend/src/main/resources/db/migration/*.sql` | Giữ nguyên 14 file, KHÔNG sửa (đã đúng) |
| `apps/backend/src/main/java/.../common/config/TenantConfig.java` | Tạo mới — chỉ chứa constant/helper, chưa cần logic phức tạp ở Phase 0 |

## Test / Gate

1. `mvn clean verify` — Flyway sẽ tự chạy migration khi Spring context khởi động test (`@SpringBootTest`
   với Testcontainers Postgres, KHÔNG dùng H2 — H2 không hỗ trợ `PARTITION BY RANGE`, `GENERATED ALWAYS
   AS ... STORED` theo đúng cú pháp Postgres, sẽ báo lỗi sai khác so với môi trường thật).
2. Deploy lên Fly.io (`fly deploy`), xem log phải thấy:
   ```
   Flyway Community Edition ... by Redgate
   Successfully validated 14 migrations
   Migrating schema "app" to version "13" ...
   Successfully applied 14 migrations
   ```
3. Query thủ công qua `psql` (hoặc Neon SQL editor):
   ```sql
   SELECT table_schema, count(*) FROM information_schema.tables
   WHERE table_schema IN ('app','audit','archive') GROUP BY table_schema;
   ```
   Kỳ vọng: `app` ≈ 39 bảng (kể cả bảng con partition), `audit`/`archive` có thể trống ban đầu.

## Lesson placeholder (điền sau khi làm xong)

```
Phase 0 — <PASS/FAIL> — ngày <...>
Điều đúng: ...
Điều vướng: ...
```
