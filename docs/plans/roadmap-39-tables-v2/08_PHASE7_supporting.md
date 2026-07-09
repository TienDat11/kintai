# PHASE 7 — Tính năng phụ trợ (notification / file_attachment / refresh_token / approval_workflow / project / timesheet / company_setting / holiday)

## Business context
Phase này KHÔNG chứa nghiệp vụ khó như Payroll — đây là các bảng "hạ tầng tính năng" hỗ trợ trải
nghiệm dùng thật, không block core HR/payroll. Gom vào 1 phase vì mỗi bảng đơn giản, độc lập nhau:
- **`notification`** — thông báo (đơn nghỉ được duyệt, lương đã publish...) — dùng `@Async` +
  WebSocket (`WebSocketConfig` đã có sẵn placeholder, phase này nối logic thật vào).
- **`file_attachment`** — đính kèm file generic (`entity_type`/`entity_id` là polymorphic reference,
  vd đính kèm giấy khám bệnh vào `leave_request`, biên lai vào `contract`). MultipartFile upload.
- **`refresh_token`** — hiện `AuthServiceImpl` có refresh token nhưng **không lưu DB** (không revoke
  được khi logout/đổi máy) — bảng này để lưu `token_hash` + `revoked_at`, sửa lại `AuthServiceImpl`
  để check revoke trước khi cấp access token mới.
- **`approval_workflow`** — workflow duyệt generic, dùng chung cho `leave_request` (Phase 4) VÀ các
  entity khác sau này (đa cấp duyệt — hiện leave chỉ có 1 approver, bảng này chuẩn bị cho multi-step
  approval nếu công ty lớn cần, nhưng **KHÔNG bắt buộc tích hợp ngay** — chỉ cần entity/mapper tồn
  tại, tích hợp thật để Phase sau nếu cần, tránh scope creep).
- **`project`**, **`timesheet`**, **`timesheet_entry`** — chấm công theo dự án (khác `attendance_record`
  theo ngày làm việc chung) — công ty outsource Nhật hay tính giờ theo project để invoice khách hàng.
- **`company_setting`** — key-value config (vd giờ chuẩn tháng, ngưỡng OT) — dùng để loại bỏ hardcode
  "160h" ở Phase 5 nếu có thời gian refactor lại (ghi rõ trong lesson nếu không kịp).
- **`holiday`** — ngày lễ Nhật (đã seed 2026-2030 ở V11) — dùng trong Attendance để biết ngày nghỉ
  lễ (làm ngày lễ → hệ số 1.35x như đã nói Phase 5).

## Scope — bảng (V9, tham chiếu V11 seed)
`app.notification`, `app.file_attachment`, `app.refresh_token`, `app.approval_workflow`,
`app.project`, `app.timesheet`, `app.timesheet_entry`, `app.company_setting`, `app.holiday`

## Việc cụ thể (ưu tiên theo mức độ cần cho nghiệp vụ đã làm)
1. **`refresh_token`** (ưu tiên cao nhất — vá lỗ hổng security thật): sửa `AuthServiceImpl.refresh()`
   để lưu hash token khi login, check `revoked_at IS NULL` khi refresh, thêm `logout()` set
   `revoked_at = now()`.
2. **`holiday`**: `HolidayMapper.isHoliday(date)` — nối vào `AttendanceServiceImpl` (Phase 3) để tính
   đúng hệ số 1.35x ngày lễ ở Payroll (Phase 5) — quay lại sửa `PayrollCalculationService` nếu Phase
   5 đã xong trước khi làm phase này.
3. **`notification`**: `NotificationService.notify(recipientId, type, title, body)` — gọi từ
   `LeaveServiceImpl.approve()` (Phase 4) và `PayrollRunService.publishPayroll()` (Phase 5) — nối
   ngược lại các Service cũ.
4. **`file_attachment`**: generic upload service, endpoint `POST /api/v1/attachments?entityType=X&entityId=Y`.
5. **`project`/`timesheet`/`timesheet_entry`**, **`approval_workflow`**, **`company_setting`**: CRUD
   đơn giản, làm sau cùng nếu còn thời gian — không có logic phức tạp, không block gate tổng.

## File cụ thể
| File | Hành động |
|---|---|
| `common/security/entity/RefreshTokenEntity.java` + mapper | Tạo mới |
| `auth/service/AuthServiceImpl.java` | Sửa — lưu/check/revoke refresh token |
| `common/entity/HolidayEntity.java` + mapper | Tạo mới |
| `attendance/service/AttendanceServiceImpl.java`, `payroll/service/PayrollCalculationService.java` | Sửa — nối holiday check |
| `common/notification/NotificationEntity.java` + mapper + `NotificationService.java` | Tạo mới |
| `leave/service/LeaveServiceImpl.java`, `payroll/service/PayrollRunService.java` | Sửa — gọi notify |
| `common/file/FileAttachmentEntity.java` + mapper + Service + Controller | Tạo mới |
| `project/`, `timesheet/`, `common/config/CompanySettingEntity.java`, `approval_workflow` | Tạo mới (CRUD đơn giản) |

## Test / Gate
- Unit test refresh token revoke: revoke rồi gọi refresh → phải bị từ chối.
- Unit test holiday: ngày lễ đã seed → `isHoliday()` trả true đúng.
- Integration test notification: approve leave → verify có record `notification` mới insert.
- `mvn clean verify` xanh.

## Lesson placeholder
```
Phase 7 — <PASS/FAIL> — ngày <...>
Điều đúng: ...
Điều vướng: ...
```
