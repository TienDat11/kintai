package com.company.kintai.employee.mapper;

import com.company.kintai.employee.entity.CommuteRouteEntity;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommuteRouteMapper {
    Optional<CommuteRouteEntity> selectById(@Param("id") Long id);
    List<CommuteRouteEntity> selectByEmployeeId(@Param("employeeId") Long employeeId);
    int insert(CommuteRouteEntity entity);
    int update(CommuteRouteEntity entity);
    int deleteById(@Param("id") Long id);
}