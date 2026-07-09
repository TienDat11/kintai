# PHASE 5 — Payroll Engine (payroll_run / payroll_item / tax_rate / social_insurance_rate)

## ⚠️ Đây là phần khó nhất #1 trong toàn bộ roadmap
Đây chính là lý do các công ty HRM Nhật thuê dev riêng — công thức tính lương không đơn giản như
`StandardPayrollCalculation` demo hiện tại (`baseSalary/160h * 1.25`). Đọc kỹ business context trước
khi code, đừng nhảy thẳng vào Entity.

## Business context
- **`payroll_run`** = "kỳ chạy lương" (1 tháng), có state machine: `DRAFT → PROCESSING → PUBLISHED →
  PAID`. Chỉ ở `DRAFT`/`PROCESSING` mới sửa được — sau `PUBLISHED` bị **trigger immutability** (V6:
  `tg_payroll_run_immutable`) chặn UPDATE/DELETE theo 労基法 Điều 108 (sổ lương phải lưu giữ và không
  được sửa sau khi đã công bố — đây là luật, không phải tùy chọn kỹ thuật).
- **`payroll_item`** = lương của **1 nhân viên trong 1 kỳ**, PARTITION theo `pay_period_start`.
  `gross_pay` và `net_pay` là **GENERATED COLUMN** (Postgres tự tính, Java KHÔNG BAO GIỜ SET giá trị
  này khi INSERT/UPDATE — nếu cố SET sẽ lỗi SQL "cannot insert into generated column"). Công thức:
  ```
  gross_pay = base_salary + overtime_pay + bonus_pay + commute_allowance + total_allowances
  net_pay   = gross_pay - total_deductions
  ```
  Java chỉ cần tính đúng `overtime_pay`, `total_allowances` (từ Phase 6), `total_deductions` (từ
  Phase 6) rồi INSERT — Postgres tự cộng ra gross/net, **không tính lại bằng Java** (single source
  of truth, tránh lệch số giữa Java và DB).
- **Công thức overtime (残業代)** theo 労基法:
  - Giờ làm vượt 8h/ngày hoặc 40h/tuần → hệ số **1.25x** lương giờ thường.
  - Làm đêm (深夜, 22h-5h) → cộng thêm **1.25x** (tức làm đêm + overtime = 1.5x).
  - Làm ngày nghỉ (休日出勤) → **1.35x**.
  - Lương giờ = `base_salary / (tiêu chuẩn giờ làm/tháng)` — số giờ chuẩn tháng thường là **160h**
    (không phải hardcode như code demo — phải tính từ `contract` + `shift` thật của tháng đó qua số
    ngày làm việc trong tháng, đây là lý do Payroll PHẢI đứng SAU Attendance ở Phase 3).
- **`tax_rate`**: thuế thu nhập Nhật theo **7 bậc lũy tiến** (progressive bracket) — không phải 1 tỷ
  lệ cố định. Công thức: tìm bracket chứa `gross_pay` trong khoảng `[min, max)`, thuế =
  `gross_pay * rate - deduction` (V8 đã seed sẵn 7 bậc + deduction offset theo luật 2026).
- **`social_insurance_rate`**: 5 loại bảo hiểm (健康保険 y tế/厚生年金 hưu trí/雇用保険 thất nghiệp/
  介護保険 chăm sóc/労災保険 tai nạn lao động), mỗi loại **chia đôi employee/employer** theo tỷ lệ
  khác nhau — employee chỉ bị trừ phần của mình vào `total_deductions`, phần employer không hiện
  trong `payroll_item` (chỉ dùng cho báo cáo công ty, ngoài scope Employee-facing).

## Scope — bảng (V6, V8)
`app.payroll_run`, `app.payroll_item` (partitioned), `app.tax_rate`, `app.social_insurance_rate`

## Việc cụ thể
- Entity mới: `PayrollRunEntity`, `PayrollItemEntity` (field KHÔNG có `grossPay`/`netPay` setter dùng
  khi insert — hoặc để MyBatis map field đó là read-only, chỉ dùng khi SELECT).
- `PayrollCalculationService` (thay thế `StandardPayrollCalculation` demo cũ hoàn toàn):
  1. `calculateOvertimePay(employeeId, periodStart, periodEnd)` — query `attendance_record` (Phase 3)
     tính tổng giờ làm thực tế, so với giờ chuẩn theo `contract.payment_type`, áp hệ số 1.25/1.35/1.5
     theo giờ/loại ngày đúng như business context trên. **Trả về `BigDecimal`**, không `double`.
  2. `calculateIncomeTax(grossPay, fiscalYear)` — query `tax_rate` tìm đúng bracket, áp công thức.
  3. `calculateSocialInsurance(grossPay)` — query `social_insurance_rate`, tính phần employee của
     mỗi loại bảo hiểm, tổng lại.
  4. `runPayroll(payrollRunId)` — Service tầng cao: lặp mọi employee active, gọi 3 hàm trên, INSERT
     `payroll_item` (không set gross/net), set `payroll_run.status = PROCESSING`.
  5. `publishPayroll(payrollRunId)` — set `status = PUBLISHED`. Sau bước này KHÔNG được sửa nữa (đã
     có trigger DB chặn, nhưng Service cũng nên tự check status trước để trả lỗi nghiệp vụ rõ ràng
     thay vì để Postgres ném lỗi thô).
- Xoá hoàn toàn `payroll/domain/PayrollItem.java` (record demo), `PayrollCalculationStrategy.java`,
  `StandardPayrollCalculation.java` — code demo không có DB IO, không dùng được nữa.

## File cụ thể
| File | Hành động |
|---|---|
| `payroll/domain/PayrollItem.java`, `PayrollCalculationStrategy.java`, `StandardPayrollCalculation.java` | Xoá |
| `payroll/entity/{PayrollRun,PayrollItem}Entity.java` | Tạo mới |
| `payroll/entity/{TaxRate,SocialInsuranceRate}Entity.java` | Tạo mới |
| `payroll/mapper/*.xml` (4 mapper) | Tạo mới |
| `payroll/service/PayrollCalculationService.java`/Impl | Tạo mới — 3 hàm tính overtime/tax/insurance |
| `payroll/service/PayrollRunService.java`/Impl | Tạo mới — orchestrate runPayroll/publishPayroll |
| `payroll/controller/PayrollController.java` | Viết lại |

## Test / Gate (bắt buộc test kỹ nhất trong toàn roadmap vì sai số = sai lương thật)
- Unit test overtime: nhân viên làm 9h/ngày (1h overtime) × 20 ngày → verify đúng số tiền theo hệ số
  1.25, so khớp bằng tay 1 case cụ thể (viết ra số cụ thể trong assert, không chỉ check > 0).
- Unit test tax bracket: gross_pay ở đúng biên giữa 2 bậc → verify chọn đúng bậc (test boundary).
- Unit test: gọi `publishPayroll` 2 lần → lần 2 phải bị chặn (status đã PUBLISHED).
- Integration test: insert `payroll_item` với `gross_pay` cố set giá trị → PHẢI lỗi (verify generated
  column không cho ghi đè, xác nhận đúng hành vi DB).
- `mvn clean verify` xanh.

## Lesson placeholder
```
Phase 5 — <PASS/FAIL> — ngày <...>
Điều đúng: ...
Điều vướng: ...
```
