package com.company.kintai.report.controller;

import com.company.kintai.common.constant.ApiPath;
import com.company.kintai.report.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.REPORTS)
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/attendance/csv")
    public void exportAttendanceCsv(
            HttpServletResponse response,
            @RequestParam int year,
            @RequestParam int month)
            throws IOException {
        reportService.exportMonthlyAttendanceCsv(response, year, month);
    }
}
