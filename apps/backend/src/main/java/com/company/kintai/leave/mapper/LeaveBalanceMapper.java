package com.company.kintai.leave.mapper;

import com.company.kintai.leave.entity.LeaveBalanceEntity;
import java.math.BigDecimal;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LeaveBalanceMapper {

    Optional<LeaveBalanceEntity> selectByEmployeeAndYear(
            @Param("employeeId") Long employeeId,
            @Param("fiscalYear") Integer fiscalYear,
            @Param("leaveTypeId") Long leaveTypeId);

    int updateUsedDays(
            @Param("id") Long id,
            @Param("usedDays") BigDecimal usedDays,
            @Param("version") Long version,
            @Param("updatedBy") String updatedBy);
}