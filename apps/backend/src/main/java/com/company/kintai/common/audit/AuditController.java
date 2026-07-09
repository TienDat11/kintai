package com.company.kintai.common.audit;

import com.company.kintai.common.audit.entity.AuditLogDetailEntity;
import com.company.kintai.common.audit.entity.AuditLogEntity;
import com.company.kintai.common.audit.mapper.AuditLogDetailMapper;
import com.company.kintai.common.audit.mapper.AuditLogMapper;
import com.company.kintai.common.dto.ApiResponse;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private final AuditLogMapper auditLogMapper;
    private final AuditLogDetailMapper auditLogDetailMapper;

    public AuditController(AuditLogMapper auditLogMapper, AuditLogDetailMapper auditLogDetailMapper) {
        this.auditLogMapper = auditLogMapper;
        this.auditLogDetailMapper = auditLogDetailMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('audit:read')")
    public ApiResponse<List<AuditLogEntity>> getAuditLogs(
            @RequestParam("table") String table,
            @RequestParam("recordId") Long recordId) {
        List<AuditLogEntity> logs = auditLogMapper.selectByTableAndRecord(table, recordId);
        return ApiResponse.ok(logs);
    }

    @GetMapping("/{auditId}/detail")
    @PreAuthorize("hasAuthority('audit:read')")
    public ApiResponse<AuditLogDetailEntity> getAuditDetail(
            @PathVariable Long auditId,
            @RequestParam("occurredAt") String occurredAt) {
        OffsetDateTime odt = OffsetDateTime.parse(occurredAt);
        AuditLogDetailEntity detail = auditLogDetailMapper.selectDetail(auditId, odt);
        return ApiResponse.ok(detail);
    }
}
