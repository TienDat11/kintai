package com.company.kintai.employee.mapper;

import com.company.kintai.employee.dto.EmployeeSearchForm;
import com.company.kintai.employee.entity.EmployeeEntity;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmployeeMapper {

    Optional<EmployeeEntity> selectById(@Param("id") Long id);

    List<EmployeeEntity> selectBySearch(
            @Param("f") EmployeeSearchForm form,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countBySearch(@Param("f") EmployeeSearchForm form);

    boolean existsByCode(@Param("code") String code);

    int insert(EmployeeEntity entity);

    int update(EmployeeEntity entity);

    int deleteById(@Param("id") Long id);
}