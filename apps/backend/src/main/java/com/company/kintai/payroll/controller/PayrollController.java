package com.company.kintai.payroll.controller;

import com.company.kintai.common.constant.ApiPath;
import com.company.kintai.common.dto.ApiResponse;
import com.company.kintai.payroll.domain.PayrollItem;
import com.company.kintai.payroll.service.PayrollService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.PAYROLL)
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<PayrollItem>> calculatePayroll(
            @PathVariable Long employeeId,
            @RequestParam int year,
            @RequestParam int month) {
        return ApiResponse.ok(payrollService.calculateMonthlyPayroll(employeeId, year, month));
    }
}
