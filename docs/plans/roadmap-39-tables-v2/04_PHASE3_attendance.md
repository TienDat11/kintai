# PHASE 3 — Chấm công (shift / shift_assignment / attendance_record / break_record)

## Business context
Code demo hiện tại (`AttendanceServiceImpl`) chỉ có check-in/check-out đơn giản, `break_minutes` là
1 số int. Thực tế Nhật phức tạp hơn:
- **Ca làm việc (`shift`)** không cố định 9h-18h cho mọi người — có ca sớm/ca đêm (深夜勤務 làm việc
  đêm có phụ cấp riêng ở Payroll). `shift_assignment` gán ca cho từng nhân viên từng ngày (lịch trực).
- **`attendance_record` PARTITION theo tháng (`work_date`)** — vì dữ liệu chấm công tăng vô hạn theo
  thời gian (mỗi nhân viên × mỗi ngày làm việc), partition giúp query 1 tháng không phải scan toàn
  bảng nhiều năm. Đây là kỹ thuật DB thật ở scale doanh nghiệp, không phải lý thuyết suông.
- **`break_record` tách riêng khỏi `attendance_record`** vì 1 ngày có thể nghỉ nhiều lần (nghỉ trưa +
  nghỉ giải lao) — 労基法 Điều 34 quy định nghỉ giải lao tối thiểu theo giờ làm (≥6h làm → ≥45p nghỉ,
  ≥8h làm → ≥60p nghỉ), `ck_break_window` trong V4 giới hạn tối đa 4h/lần nghỉ để tránh nhập sai.
- `status` enum tiếng Nhật (出勤 đi làm/欠勤 vắng/遅刻 đi muộn/早退 về sớm/有給 nghỉ có lương/代休 nghỉ bù/
  振休 đổi ca nghỉ/休業 tạm nghỉ) — đây LÀ business rule Nhật, không dịch sang tiếng Anh trong DB.

## Scope — bảng (V4)
`app.shift`, `app.shift_assignment`, `app.attendance_record` (partitioned by `work_date`),
`app.break_record` (partitioned by `work_date`, FK `attendance_id`)

## Việc cụ thể
- Xoá `attendance/entity/AttendanceRecordEntity.java` cũ (sai cột, không có partition key rõ ràng).
- Entity mới PHẢI có field `workMonth`/`workYear` khớp generated column trong V4 (Entity chỉ **đọc**,
  không bao giờ SET giá trị 2 cột này khi INSERT — để Postgres tự generate).
- Mapper XML: MỌI câu SELECT/UPDATE/DELETE trên `attendance_record`/`break_record` PHẢI có
  `work_date` (hoặc range `work_date`) trong `WHERE` — đây là nguyên tắc #6 của ROADMAP, thiếu nó thì
  Postgres phải quét toàn bộ mọi partition (mất hết lợi ích partition, hiệu năng tệ).
- `ShiftAssignmentMapper`: `selectShiftForEmployeeOnDate(employeeId, date)` — dùng ở Service để biết
  giờ chuẩn của ca, từ đó tính `lateMinutes`/`earlyLeaveMinutes` khi check-in/out lệch giờ ca.
- `AttendanceServiceImpl` viết lại: `checkIn()` phải tìm `shift_assignment` hôm nay của employee, so
  `check_in` thực tế với `shift.start_time` để tự set `status = 遅刻` nếu muộn >0 phút (business rule
  thật, không phải chỉ ghi giờ suông).
- `break_record`: `startBreak()`/`endBreak()` mới — check tổng break time trong ngày không vượt
  `ck_break_window` (4h) trước khi insert, trả lỗi nghiệp vụ rõ ràng nếu vi phạm.

## File cụ thể
| File | Hành động |
|---|---|
| `attendance/entity/AttendanceRecordEntity.java` | Xoá, viết lại (thêm workMonth/workYear, status enum) |
| `attendance/entity/{Shift,ShiftAssignment,BreakRecord}Entity.java` | Tạo mới |
| `attendance/mapper/*.xml` (4 mapper) | Viết lại/tạo mới — mọi query có `work_date` trong WHERE |
| `attendance/domain/{Shift,BreakRecord}.java` | Tạo mới |
| `common/constant/AttendanceStatus.java` (enum tiếng Nhật) | Tạo mới |
| `attendance/service/AttendanceServiceImpl.java` | Viết lại — logic so sánh giờ ca |

## Test / Gate
- Unit test: check-in muộn 10 phút so với `shift.start_time` → status phải là `遅刻`.
- Unit test: tổng break time 250 phút (>240) → phải reject với `BusinessException`.
- Integration test Testcontainers: insert 2 attendance_record ở 2 tháng khác nhau → query theo range
  1 tháng chỉ trả đúng record của tháng đó (verify partition pruning hoạt động đúng).
- `mvn clean verify` xanh.

## Lesson placeholder
```
Phase 3 — <PASS/FAIL> — ngày <...>
Điều đúng: ...
Điều vướng: ...
```
