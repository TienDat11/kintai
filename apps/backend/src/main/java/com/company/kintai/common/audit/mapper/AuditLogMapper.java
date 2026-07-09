package com.company.kintai.common.audit.mapper;

import com.company.kintai.common.audit.entity.AuditLogEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuditLogMapper {

    List<AuditLogEntity> selectByTableAndRecord(
            @Param("tableName") String tableName,
            @Param("recordId") Long recordId);
}
