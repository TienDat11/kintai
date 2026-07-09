package com.company.kintai.common.audit.entity;

import java.time.LocalDateTime;

/**
 * Bản đọc (READ-ONLY) của bảng {@code app.audit_log} (V10__audit_log_split.sql).
 *
 * <p>Bảng này CHỈ được ghi bởi trigger DB {@code app.tg_audit()} (đặt tên {@code audit_t_*} sau
 * V13). Java KHÔNG BAO GIỜ tự INSERT/UPDATE/DELETE vào bảng này — mapper tương ứng
 * ({@code AuditLogMapper}) chỉ có method SELECT.
 *
 * <p>{@code occurredAt} là cột partition key (PARTITION BY RANGE (occurred_at)) — mọi query trên
 * bảng này phải luôn có điều kiện range trên cột này để Postgres áp dụng partition pruning.
 */
public class AuditLogEntity {

    private Long id;
    private LocalDateTime occurredAt;
    private String tableName;
    private Long recordId;
    /** 'I' (INSERT) / 'U' (UPDATE) / 'D' (DELETE) — xem CHECK constraint trong V10. */
    private String operation;

    private String changedBy;
    private String clientIp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
}
