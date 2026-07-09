# PHASE 8 — Audit trail + Partition maintenance (đóng vòng)

## Business context
- **`audit_log`** (compact, PARTITION theo `occurred_at`) — ghi MỌI thay đổi (I/U/D) trên 34 bảng
  nghiệp vụ, tự động qua **trigger DB** (`audit_t_*`, V10 đã tạo, không cần Java code viết trigger —
  Java chỉ cần đọc). Ghi compact (không JSONB) để giữ kích thước nhỏ (~73MB/5 năm theo comment V10) —
  vì Neon free tier giới hạn 0.5GB, đây là quyết định kỹ thuật thật đã tính toán trước.
- **`audit_log_detail`** (PARTITION, JSONB old/new data) — CHỈ ghi cho bảng chứa PII (employee,
  payroll_item, bank_account, dependent_family, contract, payroll_run, year_end_adjustment,
  bonus_payment) — vì so sánh before/after đầy đủ chỉ cần cho dữ liệu nhạy cảm/quan trọng (tuân thủ +
  điều tra khi có tranh chấp lương), không cần cho bảng như `notification`.
- **Partition maintenance** (`app.ensure_future_partitions()`, `app.detach_old_partitions()`, V12) —
  đã có sẵn function SQL, nhưng **chưa có ai gọi chúng theo lịch** — cần 1 Spring `@Scheduled` job
  gọi hàng tháng, nếu không gọi thì tới tháng không có partition mới → INSERT sẽ lỗi "no partition
  found for row" (đây là lỗi vận hành thật rất hay gặp với bảng partition theo thời gian).

## Scope — bảng + hạ tầng (V10, V12, V13)
`app.audit_log`, `app.audit_log_detail` — chỉ cần đọc (Java KHÔNG BAO GIỜ tự INSERT vào 2 bảng này,
trigger DB tự làm) + 1 scheduler job gọi 2 function partition maintenance.

## Việc cụ thể
1. **Audit read API** (cho ADMIN/AUDITOR xem lịch sử thay đổi):
   - `AuditLogMapper.selectByTableAndRecord(tableName, recordId)` — trả compact log.
   - `AuditLogDetailMapper.selectDetail(auditId, occurredAt)` — trả JSONB old/new (chỉ PII table).
   - Controller mới `AuditController` — `GET /api/v1/audit?table=employee&recordId=5`, PHẢI có
     `@PreAuthorize("hasAuthority('audit:read')")` (permission đã seed ở V1 Phase 1).
2. **Partition maintenance scheduler**:
   - `PartitionMaintenanceScheduler` — `@Scheduled(cron = "0 0 3 1 * *")` (3h sáng ngày 1 hàng tháng,
     giờ thấp điểm), gọi `SELECT app.ensure_future_partitions(3)` (tạo trước 3 tháng) rồi
     `SELECT app.detach_old_partitions(24)` (dọn partition cũ >24 tháng sang schema `archive`).
   - Log kết quả rõ ràng (KHÔNG dùng `System.out.println` — dùng logger, theo R10).
   - Viết endpoint `POST /api/v1/admin/partition-maintenance/run` để **trigger thủ công** khi cần
     test hoặc chạy gấp (không phải đợi đúng lịch cron) — chỉ ADMIN gọi được.
3. **Kiểm tra RLS FORCE trên partition con** — V12 đã có RLS FORCE propagation, chỉ cần
   integration test xác nhận (không cần code Java thêm).

## File cụ thể
| File | Hành động |
|---|---|
| `common/audit/entity/{AuditLog,AuditLogDetail}Entity.java` | Tạo mới |
| `common/audit/mapper/*.xml` | Tạo mới (chỉ SELECT) |
| `common/audit/AuditController.java` | Tạo mới |
| `common/scheduler/PartitionMaintenanceScheduler.java` | Tạo mới |
| `common/config/AdminController.java` (hoặc thêm vào audit controller) | Tạo mới — endpoint trigger thủ công |

## Test / Gate
- Integration test (Testcontainers): UPDATE 1 dòng `employee` → verify tự động có 1 record mới
  trong `audit_log` VÀ `audit_log_detail` (vì employee là PII table) với đúng `old_data`/`new_data`.
- Integration test: UPDATE 1 dòng bảng non-PII (vd `notification`) → verify có `audit_log` nhưng
  KHÔNG có `audit_log_detail` (đúng logic V10 chỉ ghi detail cho PII).
- Integration test: gọi `ensure_future_partitions` qua Service → verify partition tháng sau đã tồn
  tại (query `information_schema.tables` sau khi gọi).
- `mvn clean verify` xanh — **đây là gate cuối cùng của cả roadmap 39 bảng**, sau phase này nên chạy
  lại toàn bộ regression test của Phase 1-7 một lần nữa để chắc không phase nào bị phase sau phá vỡ.

## Lesson placeholder
```
Phase 8 — <PASS/FAIL> — ngày <...>
Điều đúng: ...
Điều vướng: ...
```

---

## Sau khi xong Phase 8 — Definition of Done toàn roadmap
- [ ] Cả 9 phase đều PASS gate riêng (`mvn clean verify` xanh từng phase).
- [ ] `LESSONS.md` có đủ 9 entry (kể cả phase có vướng, không chỉ ghi phase pass).
- [ ] Deploy lại lên Fly.io, xác nhận Flyway log 14 migration apply thành công trên Neon thật.
- [ ] Test thủ công 1 luồng end-to-end thật: tạo employee → chấm công 1 tháng → xin nghỉ → duyệt →
  chạy payroll → publish → xem payslip có số đúng tay tính lại được.
- [ ] README cập nhật lại đúng sơ đồ 39 bảng (không còn mô tả 5 bảng demo cũ).
