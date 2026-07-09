package com.company.kintai.common.audit.mapper;

import com.company.kintai.common.audit.entity.AuditLogDetailEntity;
import java.time.OffsetDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuditLogDetailMapper {

    AuditLogDetailEntity selectDetail(
            @Param("auditId") Long auditId,
            @Param("occurredAt") OffsetDateTime occurredAt);
}
