# ROADMAP — Từ 5 bảng demo lên 39 bảng nghiệp vụ HRM thật

> **Đọc file này ĐẦU TIÊN ở session mới.** File này là bản đồ tổng, mỗi phase có file riêng
> `0X_PHASE_...md` trong cùng thư mục `docs/plans/roadmap-39-tables-v2/`. Đọc theo đúng thứ tự số.

## 0. Tình trạng hiện tại (đã xác nhận qua Discovery — 2026-07-01)

- Backend đã **deploy thành công lên Fly.io** (`kintai-backend.fly.dev`), 2 machine `nrt`, health check
  qua Spring Boot Actuator, profile `prod` đọc `DB_URL/DB_USER/DB_PASSWORD` từ Neon Postgres.
- Neon Postgres đã có **14 file Flyway migration** (`V0__init_schemas_roles.sql` → `V13__trigger_rename.sql`)
  định nghĩa **39 bảng nghiệp vụ thật** trong schema `app` (+ `audit`, `archive`), có RLS, partition theo
  tháng, generated column, trigger immutability theo 労基法 (Luật Lao động Nhật).
- **NHƯNG**: dù `pom.xml` **đã có dependency Flyway** (flyway-core + flyway-database-postgresql), 14 file migration này **chưa từng được chạy** trên Neon.
  Neon hiện đang **rỗng hoàn toàn** hoặc (nếu bạn vừa chạy `schema.sql` cũ) đang có **5 bảng demo H2**
  (`app_user`, `employee`, `attendance_record`, `leave_request`, `department` — không có prefix `app.`,
  dùng `deleted BOOLEAN` thay vì `deleted_at TIMESTAMPTZ`).
- Code Java hiện tại (`auth/`, `employee/`, `attendance/`, `leave/`, `payroll/`, `report/`) viết cho
  schema demo 5 bảng đó — **không khớp với 39 bảng thật**. `payroll/` và `report/` hiện là **demo thuần**,
  không đọc/viết DB.

**Kết luận:** đây không phải "thêm code" mà là **build lại toàn bộ tầng persistence + service** theo
schema thật, đồng thời **dọn code demo cũ**. Đây chính là phần nghiệp vụ khó — lý do các công ty HRM
Nhật thuê dev riêng để làm (RBAC động, payroll với luật thuế/bảo hiểm, immutability theo luật, audit log
tách PII, partition theo thời gian).

## 1. Nguyên tắc xuyên suốt mọi phase (đọc trước khi làm bất kỳ phase nào)

1. **Domain thuần (`domain/Xxx.java`) KHÔNG đổi theo DB** — chỉ `Entity` (MyBatis mapping) và `Mapper.xml`
   đổi theo cột thật. Domain là record Java thuần, Service trả domain ra Controller (theo `PLAN_00_RULES.md` R6).
2. **Mọi cột `deleted_at TIMESTAMPTZ`** (không phải boolean) → mọi mapper SELECT phải có
   `WHERE deleted_at IS NULL`; DELETE thực chất là `UPDATE ... SET deleted_at = now()` (soft delete).
3. **Mọi bảng có `version BIGINT`** → optimistic lock: UPDATE phải có `WHERE version = #{version}` và
   tăng `version = version + 1`, nếu 0 row affected → ném `OptimisticLockException`/`BusinessException`.
4. **`created_by`/`updated_by`** lấy từ `SecurityContextHolder` (JWT subject), KHÔNG hardcode.
5. **RLS (Row Level Security)** trên nhiều bảng dùng `current_setting('app.user_id')`/tenant — nghĩa là
   mọi connection từ HikariCP phải `SET app.user_id = ...` đầu mỗi transaction. Cần 1
   `ConnectionCustomizer`/`@Around` interceptor ở Phase 1 — làm 1 lần, dùng lại mọi phase sau.
6. **Bảng partition theo thời gian** (`attendance_record`, `break_record`, `payroll_item`, `audit_log*`)
   → Mapper KHÔNG được query mà thiếu cột partition key trong `WHERE` (Postgres partition pruning), và
   INSERT luôn phải có cột đó để router chọn đúng partition con.
7. **Mọi số tiền dùng `BigDecimal`**, KHÔNG dùng `double`/`float` (sai số làm sai lương → lỗi nghiệp vụ
   nghiêm trọng nhất trong payroll).
8. **Sau mỗi phase**: chạy `mvn clean verify`, viết lesson (Opus), rồi mới sang phase kế — đúng
   `PLAN_00_RULES.md` R9 (Definition of Done) và nguyên tắc maestro (loop khép kín).

## 2. Danh sách phase (đọc theo thứ tự, file chi tiết trong cùng thư mục)

| Phase | File | Bảng trong scope | Vì sao đứng ở vị trí này |
|---|---|---|---|
| **0** | `01_PHASE0_flyway_infra.md` | (không có bảng mới) | Phải chạy được 14 migration trước khi viết bất kỳ entity nào |
| **1** | `02_PHASE1_rbac_employee_core.md` | `role`, `permission`, `role_permission`, `employee_role`, `department`, `position`, `employee` | Auth + Employee là nền, mọi bảng khác FK về `employee` |
| **2** | `03_PHASE2_employee_relations.md` | `contract`, `bank_account`, `dependent_family`, `commute_route`, `emergency_contact` | Mở rộng hồ sơ nhân viên — cần mã hoá mynumber/số TK trước Phase payroll (payroll cần bank_account để trả lương) |
| **3** | `04_PHASE3_attendance.md` | `shift`, `shift_assignment`, `attendance_record`, `break_record` | Chấm công là input để tính lương ở Phase 5 |
| **4** | `05_PHASE4_leave.md` | `leave_type`, `leave_balance`, `leave_request` | Nghỉ phép ảnh hưởng lương (nghỉ có lương/không lương) |
| **5** | `06_PHASE5_payroll_core.md` | `payroll_run`, `payroll_item`, `tax_rate`, `social_insurance_rate` | **Phần khó nhất #1** — engine tính lương chính, cần attendance+leave xong trước |
| **6** | `07_PHASE6_payroll_components.md` | `allowance`, `deduction`, `bonus_payment`, `year_end_adjustment`, `resident_tax` | **Phần khó nhất #2** — chi tiết phụ cấp/khấu trừ, quyết toán cuối năm |
| **7** | `08_PHASE7_supporting.md` | `notification`, `file_attachment`, `refresh_token`, `approval_workflow`, `project`, `timesheet`, `timesheet_entry`, `company_setting`, `holiday` | Tính năng phụ trợ — không block nghiệp vụ core |
| **8** | `09_PHASE8_audit_partition_hardening.md` | `audit_log`, `audit_log_detail` + partition maintenance job | Đóng vòng — audit trail + tự động tạo/dọn partition |

**Tổng**: 9 phase, 39 bảng + hạ tầng Flyway + RLS + scheduler.

## 3. Lane routing chuẩn cho MỌI phase (Maestro v5)

Mỗi phase file con đều theo đúng khung lane này — không lặp lại giải thích ở từng file:

```
1. DISCOVERY   (Haiku)  — đọc kỹ file V*.sql liên quan phase + code cũ cần xoá/thay, KHÔNG suy đoán cột
2. ARCHITECT_PLAN (Opus) — đã viết sẵn trong file phase (bạn/Opus session mới chỉ cần review, không viết lại từ đầu)
3. VERIFY_PLAN  (Sonnet) — review plan phase trước khi Haiku code (đối chiếu cột SQL thật vs field Entity)
4. IMPLEMENT    (Haiku)  — viết Entity/Mapper.xml/Mapper interface/domain/DTO/Service/Controller theo plan
5. WRITE_TESTS  (Haiku)  — unit test Service (Mockito mock Mapper) + integration test Mapper (Testcontainers Postgres, KHÔNG dùng H2 vì có partition/RLS/generated column mà H2 không hỗ trợ giống Postgres)
6. VERIFY_CODE  (Sonnet) — check hallucination API, check optimistic lock, check soft-delete, check RLS session var
7. FIX          (Haiku)  — sửa theo lesson của Sonnet, tối đa 3 vòng
8. LESSON_WRITE (Opus)   — ghi lesson (pass hay fail đều ghi) vào `docs/plans/roadmap-39-tables-v2/LESSONS.md`
```

**Model routing khi spawn Agent** (dùng đúng theo maestro):
- DISCOVERY, IMPLEMENT, WRITE_TESTS, FIX → `model: "haiku"` (hoặc subagent mặc định của bạn nếu không có haiku riêng, miễn giữ vai "executor")
- VERIFY_PLAN, VERIFY_CODE, QA_TEST → `model: "sonnet"`
- ARCHITECT_PLAN, LESSON_WRITE, quyết định hướng đi khi Haiku BLOCKED → Opus (bạn, người điều phối chính)

## 4. Cách dùng ở session mới

1. Mở `00_ROADMAP.md` (file này) để nhớ lại toàn cảnh.
2. Mở file phase đang làm (theo thứ tự 0→8, đừng nhảy phase).
3. Copy checklist trong file phase đó vào `TaskCreate` để track.
4. Sau khi xong 1 phase: cập nhật cột "Status" bảng trên (tự thêm), ghi lesson vào `LESSONS.md`, rồi mới mở file phase kế.
5. Nếu bí ở đâu, quay lại đọc mục "Business context" trong file phase đó — mỗi phase đều có phần giải
   thích nghiệp vụ Nhật (tại sao luật yêu cầu vậy) để bạn học được, không chỉ code theo.

## 5. File LESSONS.md

Tạo trống, các phase sẽ tự ghi vào đây khi làm xong (thành công hay có vướng đều ghi):
`docs/plans/roadmap-39-tables-v2/LESSONS.md`
