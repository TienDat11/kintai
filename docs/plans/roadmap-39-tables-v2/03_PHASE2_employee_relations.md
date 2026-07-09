# PHASE 2 — Hồ sơ nhân viên mở rộng (contract / bank / dependent / commute / emergency)

## Business context
Ở Nhật, hồ sơ nhân sự không chỉ là tên-email-phone. 5 bảng phase này là những thứ HRM thật **bắt
buộc phải có** để trả lương và tuân thủ luật:
- `contract` — hợp đồng lao động có 6 loại (正社員 chính thức / 契約社員 hợp đồng / 派遣社員 派遣 /
  アルバイト / パート / khác), mỗi loại có luật thuế/bảo hiểm khác nhau ở Phase Payroll sau.
  `payment_type` (MONTHLY/HOURLY/DAILY) quyết định công thức tính lương.
- `bank_account` — **bắt buộc để trả lương** (Nhật hầu như 100% trả lương qua chuyển khoản, không
  tiền mặt). Số tài khoản PHẢI mã hoá như mynumber (cùng lý do bảo mật PII).
- `dependent_family` — người phụ thuộc (vợ/con) ảnh hưởng **số tiền miễn thuế** (扶養控除) khi tính
  thuế thu nhập cuối năm (`year_end_adjustment` ở Phase 6) — đây là lý do bảng này nằm ở Employee
  Relations chứ không phải Payroll, vì nó là *thông tin hồ sơ*, Payroll chỉ *đọc* nó.
- `commute_route` — 通勤手当 (phụ cấp đi lại) tính theo khoảng cách/giá vé tháng, là 1 phần
  `allowance` ở Phase 6, nhưng thông tin tuyến đường thuộc về hồ sơ nhân viên.
- `emergency_contact` — liên hệ khẩn cấp, không ảnh hưởng lương, đơn giản nhất trong phase này.

## Scope — bảng (V2 phần còn lại, V3 RLS)
`app.contract`, `app.bank_account`, `app.dependent_family`, `app.commute_route`,
`app.emergency_contact` — tất cả FK `employee_id`, tất cả cần RLS như `app.employee` (V3 đã
ENABLE+FORCE RLS trên các bảng liên quan tới employee).

## Việc cụ thể
- 5 cặp Entity/Mapper.xml/Mapper interface mới, đơn giản (CRUD thuần, không có logic phức tạp).
- `bank_account.account_number_encrypted`/`account_number_hash` dùng lại `CryptoService` đã viết ở
  Phase 1 (KHÔNG viết crypto riêng — đây là ví dụ thực tế của "utils dùng ≥ 2 nơi" trong
  `PLAN_00_RULES.md` R3).
- `bank_account` có UNIQUE index "chỉ 1 tài khoản `is_primary=true` mỗi employee" → Service PHẢI tự
  kiểm tra trước INSERT/UPDATE (nếu set primary mới, phải `UPDATE ... SET is_primary=false` cho các
  account khác cùng employee trong **cùng 1 transaction** — dùng `@Transactional` ở Service, không
  làm 2 câu SQL tách lẻ để tránh race condition).
- `dependent_family`, `commute_route` — soft-delete + optimistic lock chuẩn như Phase 1.
- Domain mới: `employee/domain/{Contract, BankAccount, DependentFamily, CommuteRoute,
  EmergencyContact}.java` — record thuần, `BankAccount` chỉ expose `accountNumberMasked`.
- `EmployeeController`: thêm sub-resource endpoint (vd `GET /api/v1/employees/{id}/bank-accounts`),
  KHÔNG tạo Controller riêng cho từng bảng nhỏ (tránh over-abstraction — R10 cấm interface thừa).

## File cụ thể
| File | Hành động |
|---|---|
| `employee/entity/{Contract,BankAccount,DependentFamily,CommuteRoute,EmergencyContact}Entity.java` | Tạo mới |
| `employee/mapper/*.xml` + interface tương ứng | Tạo mới |
| `employee/domain/*.java` (5 record mới) | Tạo mới |
| `employee/service/EmployeeRelationService.java`/Impl | Tạo mới — chứa logic "chỉ 1 primary bank account" |
| `employee/controller/EmployeeController.java` | Sửa — thêm sub-resource endpoint |

## Test / Gate
- Unit test logic "set primary bank account" — case: có 2 account cũ, set account thứ 3 làm primary
  → phải verify 2 account cũ bị set `is_primary=false`.
- Integration test `bank_account` UNIQUE constraint vi phạm → phải bắt được lỗi và map sang
  `BusinessException(ErrorCode.DUPLICATE)`, không để exception DB thô lộ ra client.
- `mvn clean verify` xanh.

## Lesson placeholder
```
Phase 2 — <PASS/FAIL> — ngày <...>
Điều đúng: ...
Điều vướng: ...
```
