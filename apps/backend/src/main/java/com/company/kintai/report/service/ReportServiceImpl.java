package com.company.kintai.report.service;

import com.company.kintai.common.util.CsvUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public void exportMonthlyAttendanceCsv(HttpServletResponse response, int year, int month)
            throws IOException {
        response.setContentType("text/csv;charset=MS932");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=attendance_" + year + String.format("%02d", month) + ".csv");

        var headers = List.of("社員コード", "氏名", "日付", "出勤", "退勤", "休憩(分)", "ステータス");
        var rows =
                List.<List<String>>of(
                        List.of("EMP001", "田中太郎", "2024-06-01", "09:00", "18:00", "60", "出勤"),
                        List.of("EMP002", "佐藤花子", "2024-06-01", "09:15", "18:30", "60", "遅刻"));

        CsvUtil.writeShiftJis(response.getOutputStream(), headers, rows);
    }
}
