package com.company.kintai.common.audit.entity;

import java.time.LocalDateTime;

/**
 * Bản đọc (READ-ONLY) của bảng {@code app.audit_log_detail} (V10__audit_log_split.sql).
 *
 * <p>Bảng này chỉ có dữ liệu cho các bảng PII (employee, payroll_item, bank_account,
 * dependent_family, contract, payroll_run, year_end_adjustment, bonus_payment) và chỉ ghi cho
 * operation UPDATE (xem trigger {@code app.tg_audit()} trong V10). Java KHÔNG BAO GIỜ tự
 * INSERT/UPDATE/DELETE vào bảng này.
 *
 * <p>PK là composite {@code (audit_id, occurred_at)} vì bảng partition theo {@code occurred_at}
 * (FK tới {@code app.audit_log(id, occurred_at)}) — mọi query phải có {@code occurredAt} để
 * Postgres partition pruning hoạt động.
 *
 * <p>{@code oldData}/{@code newData} là cột JSONB. MyBatis không có converter JSONB &lt;-&gt; POJO
 * built-in nên map thẳng sang {@code String} (giữ nguyên JSON text trả về từ Postgres qua
 * {@code ::text} cast trong SQL của mapper XML) — client (Controller/FE) tự parse JSON nếu cần.
 */
public class AuditLogDetailEntity {

    private Long auditId;
    private LocalDateTime occurredAt;
    private String oldData;
    private String newData;

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getOldData() {
        return oldData;
    }

    public void setOldData(String oldData) {
        this.oldData = oldData;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }
}
