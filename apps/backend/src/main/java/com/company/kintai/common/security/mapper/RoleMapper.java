package com.company.kintai.common.security.mapper;

import com.company.kintai.common.security.entity.RoleEntity;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleMapper {

    Optional<RoleEntity> selectById(@Param("id") Long id);

    Optional<RoleEntity> selectByCode(@Param("code") String code);

    List<RoleEntity> selectAll();

    int insert(RoleEntity entity);

    int update(RoleEntity entity);

    int deleteById(@Param("id") Long id, @Param("currentTenantId") String currentTenantId);
}