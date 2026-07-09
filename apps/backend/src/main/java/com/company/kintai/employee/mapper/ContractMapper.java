package com.company.kintai.employee.mapper;

import com.company.kintai.employee.entity.ContractEntity;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ContractMapper {
    Optional<ContractEntity> selectById(@Param("id") Long id);
    List<ContractEntity> selectByEmployeeId(@Param("employeeId") Long employeeId);
    int insert(ContractEntity entity);
    int update(ContractEntity entity);
    int deleteById(@Param("id") Long id);
}