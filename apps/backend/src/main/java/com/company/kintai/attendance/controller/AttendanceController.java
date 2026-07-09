package com.company.kintai.attendance.controller;

import com.company.kintai.attendance.dto.AttendanceResponse;
import com.company.kintai.attendance.dto.CheckInRequest;
import com.company.kintai.attendance.dto.CheckOutRequest;
import com.company.kintai.attendance.service.AttendanceService;
import com.company.kintai.common.constant.ApiPath;
import com.company.kintai.common.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.ATTENDANCE)
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in")
    public ApiResponse<AttendanceResponse> checkIn(@Valid @RequestBody CheckInRequest request) {
        return ApiResponse.ok(attendanceService.checkIn(request));
    }

    @PostMapping("/check-out")
    public ApiResponse<AttendanceResponse> checkOut(@Valid @RequestBody CheckOutRequest request) {
        return ApiResponse.ok(attendanceService.checkOut(request));
    }

    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<AttendanceResponse>> getMonthly(
            @PathVariable Long employeeId,
            @RequestParam int year,
            @RequestParam int month) {
        return ApiResponse.ok(attendanceService.getMonthlyAttendance(employeeId, year, month));
    }
}
