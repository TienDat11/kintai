package com.company.kintai.attendance.mapper;

import com.company.kintai.attendance.entity.AttendanceRecordEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AttendanceMapper {

    Optional<AttendanceRecordEntity> selectByEmployeeAndDate(
            @Param("employeeId") Long employeeId, @Param("workDate") LocalDate workDate);

    List<AttendanceRecordEntity> selectByEmployeeAndMonth(
            @Param("employeeId") Long employeeId,
            @Param("year") int year,
            @Param("month") int month);

    int insert(AttendanceRecordEntity entity);

    int update(AttendanceRecordEntity entity);
}
