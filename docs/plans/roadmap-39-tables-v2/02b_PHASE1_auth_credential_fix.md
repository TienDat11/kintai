# PHASE 1b — Fix BLOCKED: bảng lưu password (Opus decision, ngày 2026-07-02)

## Vấn đề Haiku báo BLOCKED
14 migration V0-V13 định nghĩa 39 bảng nghiệp vụ nhưng **không có bảng nào lưu password**.
`app.employee` chỉ có `email`, không có `password_hash`. `AuthServiceImpl.login()` không có gì để
verify → Haiku đúng khi không tự bịa bảng, báo BLOCKED và throw `BusinessException(UNAUTHORIZED)` tạm.

## Quyết định (Opus)
Thêm **migration mới V14** (không sửa V0-V13 đã áp — nguyên tắc Flyway: không sửa migration đã
chạy), tạo bảng `app.user_credential`:

```sql
-- V14__auth_credential.sql
CREATE TABLE app.user_credential (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,   -- BCrypt, tự chứa salt+cost, không cần cột salt riêng
    failed_login_attempts SMALLINT NOT NULL DEFAULT 0,
    locked_until TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    FOREIGN KEY (employee_id) REFERENCES app.employee(id) ON DELETE RESTRICT
);
CREATE INDEX idx_cred_emp ON app.user_credential(employee_id) WHERE deleted_at IS NULL;

ALTER TABLE app.user_credential ENABLE ROW LEVEL SECURITY;
ALTER TABLE app.user_credential FORCE  ROW LEVEL SECURITY;

ALTER TABLE app.user_credential OWNER TO kintai_app;
```

**Lý do chọn bảng riêng (không thêm cột vào `app.employee`):**
- `employee` là PII table đã có audit detail trigger (V10) — thêm `password_hash` vào đây sẽ lộ
  password vào `audit_log_detail.old_data/new_data` JSONB mỗi lần employee bị UPDATE (vd đổi tên,
  đổi phone) → rò rỉ hash trong audit trail là rủi ro bảo mật thật.
- Tách bảng cho phép RLS/quyền riêng nếu sau này cần audit truy cập password khác với truy cập
  profile.

**Login theo email**: `app.employee.email` đã UNIQUE — dùng làm username. Query:
`SELECT e.id, e.email, c.password_hash, c.failed_login_attempts, c.locked_until FROM app.employee e
JOIN app.user_credential c ON c.employee_id = e.id WHERE e.email = :email AND e.deleted_at IS NULL
AND c.deleted_at IS NULL`.

**Password hashing**: dùng `BCryptPasswordEncoder` (Spring Security đã có sẵn dependency
`spring-boot-starter-security`) — KHÔNG tự viết crypto (khác `CryptoService` AES-GCM dùng cho
mynumber — đó là 2-way encrypt để giải mã lại, còn password phải 1-way hash, BCrypt đúng dụng cụ).

## Việc cụ thể cho Haiku
1. Tạo `apps/backend/src/main/resources/db/migration/V14__auth_credential.sql` (nội dung trên).
2. `common/security/entity/UserCredentialEntity.java` — field khớp cột trên.
3. `common/security/mapper/UserCredentialMapper.java` + `.xml` — cần:
   - `selectByEmail(String email)` → JOIN employee, trả về đủ field để verify (email, employeeId,
     passwordHash, failedLoginAttempts, lockedUntil).
   - `incrementFailedAttempts(Long employeeId)` — optimistic lock version.
   - `resetFailedAttempts(Long employeeId)`.
   - `insert(...)` — dùng khi seed tài khoản đầu (không có Controller tạo public, chỉ seed script/data.sql thủ công hoặc admin-only endpoint nếu cần).
4. Sửa `AuthServiceImpl.login()`:
   - Query `selectByEmail`.
   - Nếu không tìm thấy hoặc `lockedUntil` còn hiệu lực → `BusinessException(UNAUTHORIZED)`.
   - `BCryptPasswordEncoder.matches(rawPassword, storedHash)`:
     - Sai → `incrementFailedAttempts`, nếu đạt ngưỡng (vd 5 lần) → set `lockedUntil = now()+15min`,
       ném `UNAUTHORIZED`.
     - Đúng → `resetFailedAttempts`, lấy permission qua `EmployeeRoleMapper` (đã có từ Phase 1 chính),
       generate JWT, trả `AuthResponse`.
5. Thêm 1 dòng seed tối thiểu trong migration hoặc ghi rõ trong lesson: cần 1 tài khoản ADMIN mẫu để
   test login thủ công (password hash BCrypt của 1 chuỗi cố định ghi rõ trong comment SQL, KHÔNG
   hardcode plaintext trong code Java).

## Test / Gate
- Unit test login: sai password 5 lần liên tiếp → account bị lock, lần 6 dù đúng password cũng phải
  reject.
- Unit test login: đúng password, chưa bị lock → trả JWT có đúng permission claim.
- `mvn clean verify` xanh (đã bao gồm Phase 1 gate cũ).

## Lesson
```
Phase 1b (auth credential fix) — PASS/FAIL — 2026-07-02
Điều đúng: Opus phát hiện gap kiến trúc (roadmap 39 bảng thiếu bảng password) trước khi Haiku tự bịa.
Điều vướng: 00_ROADMAP.md liệt kê 9 phase nhưng không phase nào cover auth credential — cần bổ sung
vào roadmap chính nếu có version sau.
```
