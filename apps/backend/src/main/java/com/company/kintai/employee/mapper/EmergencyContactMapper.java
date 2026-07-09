package com.company.kintai.employee.mapper;

import com.company.kintai.employee.entity.EmergencyContactEntity;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmergencyContactMapper {
    Optional<EmergencyContactEntity> selectById(@Param("id") Long id);
    List<EmergencyContactEntity> selectByEmployeeId(@Param("employeeId") Long employeeId);
    int insert(EmergencyContactEntity entity);
    int update(EmergencyContactEntity entity);
    int deleteById(@Param("id") Long id);
}