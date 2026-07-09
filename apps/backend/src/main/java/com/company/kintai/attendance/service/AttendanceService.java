package com.company.kintai.attendance.service;

import com.company.kintai.attendance.dto.AttendanceResponse;
import com.company.kintai.attendance.dto.CheckInRequest;
import com.company.kintai.attendance.dto.CheckOutRequest;
import java.util.List;

public interface AttendanceService {

    AttendanceResponse checkIn(CheckInRequest request);

    AttendanceResponse checkOut(CheckOutRequest request);

    List<AttendanceResponse> getMonthlyAttendance(Long employeeId, int year, int month);
}
