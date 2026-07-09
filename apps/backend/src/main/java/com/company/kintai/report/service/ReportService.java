package com.company.kintai.report.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ReportService {

    void exportMonthlyAttendanceCsv(HttpServletResponse response, int year, int month) throws IOException;
}
