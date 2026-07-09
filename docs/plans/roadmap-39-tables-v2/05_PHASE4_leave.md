# PHASE 4 — Nghỉ phép (leave_type / leave_balance / leave_request)

## Business context
Code demo hiện tại lưu `leave_type` là **string tự do** trong `leave_request` — không kiểm soát
được có bao nhiêu loại nghỉ, không tính được số ngày còn lại. Thực tế Nhật:
- **`leave_type`** là bảng danh mục cố định (V5 seed 6 loại: 有給 PAID/病気 SICK/産休 MATERNITY/
  育児 CHILDCARE/忌引 BEREAVEMENT/代休 SUBSTITUTE), mỗi loại có `is_paid` (có lương hay không — ảnh
  hưởng trực tiếp tới Payroll Phase 5) và `days_per_year` (số ngày cấp theo luật).
- **`leave_balance`** là bảng **tồn ngày nghỉ theo năm tài chính** (`fiscal_year`) — mỗi
  employee+type+year có `grant` (số ngày được cấp), `used` (đã dùng), `remaining` (GENERATED =
  grant - used). Đây là bảng **quan trọng nhất về business logic**: 有給休暇 (nghỉ có lương) theo
  労基法 Điều 39 phải được cấp tự động theo số năm làm việc (6 tháng đầu 10 ngày, sau đó tăng dần) —
  không có bảng này thì không thể enforce luật, nhân viên có thể xin nghỉ vô hạn.
- **`leave_request`** đổi từ string `leave_type` sang FK `leave_type_id`, thêm `approver_id` +
  workflow status (PENDING/APPROVED/REJECTED/CANCELLED) — khi APPROVED, PHẢI trừ `leave_balance.used`
  trong cùng transaction (đây là chỗ dễ sai nhất: nếu approve mà không trừ balance, nhân viên có thể
  xin nghỉ nhiều hơn số ngày được cấp).

## Scope — bảng (V5)
`app.leave_type`, `app.leave_balance`, `app.leave_request`

## Việc cụ thể
- Xoá `leave/entity/LeaveRequestEntity.java` cũ (leave_type string).
- `LeaveTypeMapper`: chỉ cần SELECT (danh mục ít thay đổi, không cần Controller CRUD riêng — dùng
  data đã seed sẵn ở V5).
- `LeaveBalanceMapper.selectByEmployeeAndYear(employeeId, fiscalYear)` — dùng để hiển thị "còn bao
  nhiêu ngày nghỉ" trên UI trước khi nhân viên nộp đơn.
- **Logic quan trọng nhất phase này**: `LeaveServiceImpl.approve(requestId)`:
  1. Lấy `leave_request`, check status hiện tại phải là PENDING (tránh approve 2 lần).
  2. Lấy `leave_balance` tương ứng employee+type+fiscal_year của request đó.
  3. Check `remaining >= request.days` — nếu không đủ, ném `BusinessException(INSUFFICIENT_LEAVE_BALANCE)`
     (đã có sẵn `ErrorCode.INSUFFICIENT_LEAVE_BALANCE` = E101 trong code hiện tại, dùng lại).
  4. `UPDATE leave_balance SET used = used + request.days` (optimistic lock version).
  5. `UPDATE leave_request SET status='APPROVED', approver_id, approved_at`.
  6. Toàn bộ bước 4-5 PHẢI trong **1 transaction** (`@Transactional`) — nếu bước 5 lỗi phải rollback
     cả bước 4, không được để balance bị trừ mà request vẫn PENDING.
- `reject()`/`cancel()`: nếu request đã APPROVED rồi mới cancel → phải **hoàn trả** lại
  `leave_balance.used` (trừ ngược lại) — nghiệp vụ hay bị quên nhất khi code case "hủy đơn đã duyệt".
- Cấp `leave_balance` tự động đầu năm tài chính (grant theo năm làm việc) — viết `@Scheduled` job
  đơn giản (`LeaveGrantScheduler`), chạy 1 lần/năm, KHÔNG cần logic phức tạp ở phase này (để optional
  ghi TODO nếu hết thời gian, không block gate).

## File cụ thể
| File | Hành động |
|---|---|
| `leave/entity/LeaveRequestEntity.java` | Xoá, viết lại (leave_type_id, approver_id) |
| `leave/entity/{LeaveType,LeaveBalance}Entity.java` | Tạo mới |
| `leave/mapper/*.xml` (3 mapper) | Viết lại/tạo mới |
| `leave/domain/{LeaveType,LeaveBalance}.java` | Tạo mới |
| `leave/service/LeaveServiceImpl.java` | Viết lại — approve/reject/cancel có transaction đúng |
| `leave/scheduler/LeaveGrantScheduler.java` | Tạo mới (optional, ghi rõ nếu để TODO) |

## Test / Gate
- Unit test approve: balance đủ → `used` tăng đúng số ngày, status → APPROVED.
- Unit test approve: balance KHÔNG đủ → ném đúng `ErrorCode.INSUFFICIENT_LEAVE_BALANCE`, balance
  KHÔNG bị thay đổi (verify state, không chỉ verify exception).
- Unit test cancel sau khi đã approved → balance `used` phải giảm lại đúng số ngày đã hoàn.
- `mvn clean verify` xanh.

## Lesson placeholder
```
Phase 4 — <PASS/FAIL> — ngày <...>
Điều đúng: ...
Điều vướng: ...
```
