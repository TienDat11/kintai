package com.company.kintai.employee.mapper;

import com.company.kintai.employee.entity.BankAccountEntity;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BankAccountMapper {
    Optional<BankAccountEntity> selectById(@Param("id") Long id);
    List<BankAccountEntity> selectByEmployeeId(@Param("employeeId") Long employeeId);
    List<BankAccountEntity> selectPrimaryByEmployeeId(@Param("employeeId") Long employeeId);
    int insert(BankAccountEntity entity);
    int update(BankAccountEntity entity);
    int updateSetNonPrimaryByEmployeeId(@Param("employeeId") Long employeeId);
    int deleteById(@Param("id") Long id);
}