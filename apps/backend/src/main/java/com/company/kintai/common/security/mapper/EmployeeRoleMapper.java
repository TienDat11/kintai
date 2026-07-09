package com.company.kintai.common.security.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmployeeRoleMapper {

    /** 3-table JOIN: employee_role -> role_permission -> permission -> role, trả về list mã permission */
    List<String> selectPermissionsByEmployeeId(@Param("employeeId") Long employeeId,
                                               @Param("currentTenantId") String currentTenantId);
}