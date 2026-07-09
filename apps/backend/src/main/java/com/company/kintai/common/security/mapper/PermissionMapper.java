package com.company.kintai.common.security.mapper;

import com.company.kintai.common.security.entity.PermissionEntity;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PermissionMapper {

    Optional<PermissionEntity> selectById(@Param("id") Long id);

    Optional<PermissionEntity> selectByCode(@Param("code") String code);
}