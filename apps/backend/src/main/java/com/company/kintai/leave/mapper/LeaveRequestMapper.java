package com.company.kintai.leave.mapper;

import com.company.kintai.leave.entity.LeaveRequestEntity;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LeaveRequestMapper {

    Optional<LeaveRequestEntity> selectById(@Param("id") Long id);

    List<LeaveRequestEntity> selectByEmployeeId(@Param("employeeId") Long employeeId);

    List<LeaveRequestEntity> selectByStatus(@Param("status") String status);

    int insert(LeaveRequestEntity entity);

    int updateStatus(
            @Param("id") Long id,
            @Param("status") String status,
            @Param("approverId") Long approverId,
            @Param("updatedBy") String updatedBy);
}