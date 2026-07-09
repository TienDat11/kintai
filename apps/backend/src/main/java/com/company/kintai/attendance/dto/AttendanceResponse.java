package com.company.kintai.attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponse(
        Long id,
        Long employeeId,
        String employeeName,
        LocalDate workDate,
        LocalDateTime checkIn,
        LocalDateTime checkOut,
        Integer breakMinutes,
        String status) {}
