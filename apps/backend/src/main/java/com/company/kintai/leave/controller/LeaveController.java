package com.company.kintai.leave.controller;

import com.company.kintai.common.constant.ApiPath;
import com.company.kintai.common.dto.ApiResponse;
import com.company.kintai.leave.dto.ApprovalRequest;
import com.company.kintai.leave.dto.LeaveRequest;
import com.company.kintai.leave.dto.LeaveResponse;
import com.company.kintai.leave.service.LeaveService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.LEAVES)
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping
    public ApiResponse<LeaveResponse> apply(@Valid @RequestBody LeaveRequest request) {
        return ApiResponse.ok(leaveService.apply(request));
    }

    @PutMapping("/{id}/approve")
    public ApiResponse<LeaveResponse> approve(
            @PathVariable Long id, @Valid @RequestBody ApprovalRequest request) {
        return ApiResponse.ok(leaveService.approve(id, request));
    }

    @PutMapping("/{id}/reject")
    public ApiResponse<LeaveResponse> reject(
            @PathVariable Long id, @Valid @RequestBody ApprovalRequest request) {
        return ApiResponse.ok(leaveService.reject(id, request));
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<LeaveResponse> cancel(@PathVariable Long id) {
        return ApiResponse.ok(leaveService.cancel(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<LeaveResponse>> getByEmployee(@PathVariable Long employeeId) {
        return ApiResponse.ok(leaveService.getByEmployee(employeeId));
    }

    @GetMapping("/pending")
    public ApiResponse<List<LeaveResponse>> getPending() {
        return ApiResponse.ok(leaveService.getPending());
    }
}