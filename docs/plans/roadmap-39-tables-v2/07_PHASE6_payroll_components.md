# PHASE 6 — Chi tiết phụ cấp/khấu trừ/quyết toán (allowance / deduction / bonus_payment / year_end_adjustment / resident_tax)

## ⚠️ Phần khó nhất #2 — phụ thuộc Phase 5 đã xong
Phase này cung cấp `total_allowances`/`total_deductions` mà `PayrollCalculationService` (Phase 5)
cần để tính `gross_pay`/`net_pay`. Nếu làm Phase 6 trước Phase 5 sẽ không có chỗ gọi tới — đúng thứ
tự ROADMAP đã định.

## Business context
- **`allowance`** (手当) — phụ cấp CỘNG vào lương, 5 loại (住宅 nhà ở/家族 gia đình/役職 chức vụ/資格
  chứng chỉ/その他 khác). `commute_allowance` (đi lại) đã tính riêng trong `payroll_item` (Phase 5,
  dùng dữ liệu `commute_route` từ Phase 2) — 5 loại này là các phụ cấp KHÁC, cộng vào
  `total_allowances`.
- **`deduction`** (控除) — khấu trừ TRỪ khỏi lương, 6 loại: 健康保険/厚生年金/雇用保険 (3 loại bảo
  hiểm đã tính ở Phase 5 `calculateSocialInsurance`, ghi vào đây làm chi tiết dòng), 所得税 (thuế thu
  nhập, từ Phase 5 `calculateIncomeTax`), 住民税 (thuế thị dân — tính riêng ở `resident_tax`, KHÔNG
  phải thuế thu nhập quốc gia), 財形 (tiết kiệm tài sản, tự nguyện, số tiền do nhân viên đăng ký).
  **Điểm quan trọng**: Phase 5 tính TOTAL cho payroll_item, Phase 6 lưu CHI TIẾT từng dòng (payslip
  cần hiển thị breakdown, không chỉ 1 số tổng).
- **`bonus_payment`** (ボーナス) — thưởng 夏季 (mùa hè)/冬季 (mùa đông)/決算 (cuối năm tài chính)/khác,
  tính **thuế riêng** (không cộng vào `payroll_item` hàng tháng — theo luật thuế Nhật, bonus có công
  thức thuế khác lương thường, dùng bảng thuế bonus riêng, KHÔNG dùng lại `tax_rate` 7 bậc của Phase 5).
- **`year_end_adjustment`** (年末調整) — quyết toán thuế cuối năm, đây là nghiệp vụ ĐẶC THÙ NHẤT của
  Nhật: nhân viên Nhật KHÔNG tự khai thuế cuối năm như nhiều nước khác — công ty tổng hợp lại tổng
  thuế đã trừ cả năm (từ `payroll_item` × 12 tháng) so với thuế thực tế phải nộp (tính lại có xét
  `dependent_family` từ Phase 2 — có phụ thuộc thì giảm thuế), ra `refund_or_balance` (GENERATED):
  dương = hoàn tiền cho nhân viên, âm = phải nộp thêm.
- **`resident_tax`** (住民税) — thuế thị dân, KHÁC hoàn toàn thuế thu nhập quốc gia: tính dựa trên
  thu nhập NĂM TRƯỚC (không phải năm hiện tại), do địa phương (`municipality_code`) quyết định, trả
  đều 12 tháng (`monthly_amount` GENERATED = annual/12).

## Scope — bảng (V7)
`app.allowance`, `app.deduction`, `app.bonus_payment`, `app.year_end_adjustment`, `app.resident_tax`

## Việc cụ thể
- 5 Entity/Mapper mới, tất cả FK `payroll_item_id` (trừ `bonus_payment`, `year_end_adjustment` FK
  trực tiếp `employee_id` vì không gắn theo tháng cụ thể) và `resident_tax` FK `employee_id`.
- Sửa lại `PayrollCalculationService.runPayroll()` (Phase 5) — thêm bước: sau khi tính overtime, gọi
  `AllowanceMapper`/`DeductionMapper` lấy danh sách phụ cấp/khấu trừ đã đăng ký cho employee đó, SUM
  lại thành `total_allowances`/`total_deductions` TRƯỚC khi insert `payroll_item` (đây là điểm nối 2
  phase — nhắc Haiku đọc lại `payroll/service/PayrollCalculationService.java` đã viết ở Phase 5,
  KHÔNG viết class tính toán riêng biệt gây trùng logic).
- `BonusPaymentService`: tính thuế bonus riêng theo bảng thuế bonus (nếu chưa có bảng thuế bonus
  riêng trong V8, dùng tạm công thức đơn giản hoá — Opus quyết định mức đơn giản hoá nào ở
  ARCHITECT_PLAN review, KHÔNG để Haiku tự bịa bảng thuế không có trong migration).
- `YearEndAdjustmentService`: job tổng hợp cuối năm — SUM 12 tháng `payroll_item` của 1 employee,
  tính lại thuế có xét `dependent_family`, so sánh, ghi `year_end_adjustment`.

## File cụ thể
| File | Hành động |
|---|---|
| `payroll/entity/{Allowance,Deduction,BonusPayment,YearEndAdjustment,ResidentTax}Entity.java` | Tạo mới |
| `payroll/mapper/*.xml` (5 mapper) | Tạo mới |
| `payroll/service/PayrollCalculationService.java` | Sửa — nối allowance/deduction vào runPayroll |
| `payroll/service/{BonusPayment,YearEndAdjustment}Service.java`/Impl | Tạo mới |
| `payroll/controller/PayrollController.java` | Sửa — thêm endpoint bonus/year-end |

## Test / Gate
- Unit test: employee có 2 allowance (住宅+家族) + 3 deduction → `runPayroll` insert đúng
  `total_allowances`/`total_deductions` là tổng đúng.
- Unit test bonus: verify thuế bonus tính khác công thức thuế lương thường (test phải fail nếu code
  vô tình dùng lại `calculateIncomeTax` của lương thường).
- Unit test year-end: employee có 1 dependent → thuế phải nhỏ hơn case không có dependent (test
  tương đối, verify hướng đúng của business rule).
- `mvn clean verify` xanh.

## Lesson placeholder
```
Phase 6 — <PASS/FAIL> — ngày <...>
Điều đúng: ...
Điều vướng: ...
```
