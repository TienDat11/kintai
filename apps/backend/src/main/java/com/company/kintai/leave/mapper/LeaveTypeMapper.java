package com.company.kintai.leave.mapper;

import com.company.kintai.leave.entity.LeaveTypeEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LeaveTypeMapper {
    @Select("SELECT * FROM app.leave_type WHERE deleted_at IS NULL ORDER BY id")
    List<LeaveTypeEntity> selectAll();

    @Select("SELECT * FROM app.leave_type WHERE id = #{id} AND deleted_at IS NULL")
    LeaveTypeEntity selectById(Long id);
}