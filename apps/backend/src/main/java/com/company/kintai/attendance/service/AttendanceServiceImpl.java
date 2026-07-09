package com.company.kintai.attendance.service;

import com.company.kintai.attendance.dto.AttendanceResponse;
import com.company.kintai.attendance.dto.CheckInRequest;
import com.company.kintai.attendance.dto.CheckOutRequest;
import com.company.kintai.attendance.entity.AttendanceRecordEntity;
import com.company.kintai.attendance.mapper.AttendanceMapper;
import com.company.kintai.common.constant.AppConstant;
import com.company.kintai.common.constant.ErrorCode;
import com.company.kintai.common.exception.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceMapper attendanceMapper;

    public AttendanceServiceImpl(AttendanceMapper attendanceMapper) {
        this.attendanceMapper = attendanceMapper;
    }

    @Override
    @Transactional
    public AttendanceResponse checkIn(CheckInRequest request) {
        LocalDate today = LocalDate.now(AppConstant.TOKYO_ZONE);

        // 本日既に出勤チェック
        var existing = attendanceMapper.selectByEmployeeAndDate(request.employeeId(), today);
        if (existing.isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_CHECKED_IN, "Already checked in today");
        }

        var entity = new AttendanceRecordEntity();
        entity.setEmployeeId(request.employeeId());
        entity.setWorkDate(today);
        entity.setCheckIn(LocalDateTime.now(AppConstant.TOKYO_ZONE));
        entity.setBreakMinutes(0);
        entity.setStatus("PRESENT");
        attendanceMapper.insert(entity);

        return toResponse(entity);
    }

    @Override
    @Transactional
    public AttendanceResponse checkOut(CheckOutRequest request) {
        LocalDate today = LocalDate.now(AppConstant.TOKYO_ZONE);

        var entity =
                attendanceMapper
                        .selectByEmployeeAndDate(request.employeeId(), today)
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                ErrorCode.NOT_FOUND, "No check-in record for today"));

        if (entity.getCheckOut() != null) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_RULE_VIOLATION, "Already checked out today");
        }

        entity.setCheckOut(LocalDateTime.now(AppConstant.TOKYO_ZONE));
        entity.setBreakMinutes(request.breakMinutes() != null ? request.breakMinutes() : 60);
        attendanceMapper.update(entity);

        return toResponse(entity);
    }

    @Override
    public List<AttendanceResponse> getMonthlyAttendance(Long employeeId, int year, int month) {
        return attendanceMapper.selectByEmployeeAndMonth(employeeId, year, month).stream()
                .map(this::toResponse)
                .toList();
    }

    private AttendanceResponse toResponse(AttendanceRecordEntity entity) {
        return new AttendanceResponse(
                entity.getId(),
                entity.getEmployeeId(),
                null,
                entity.getWorkDate(),
                entity.getCheckIn(),
                entity.getCheckOut(),
                entity.getBreakMinutes(),
                entity.getStatus());
    }
}
