package com.company.kintai.employee.mapper;

import com.company.kintai.employee.entity.DependentFamilyEntity;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DependentFamilyMapper {
    Optional<DependentFamilyEntity> selectById(@Param("id") Long id);
    List<DependentFamilyEntity> selectByEmployeeId(@Param("employeeId") Long employeeId);
    int insert(DependentFamilyEntity entity);
    int update(DependentFamilyEntity entity);
    int deleteById(@Param("id") Long id);
}