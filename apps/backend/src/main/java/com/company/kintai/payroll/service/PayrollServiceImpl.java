package com.company.kintai.payroll.service;

import com.company.kintai.payroll.domain.PayrollItem;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PayrollServiceImpl implements PayrollService {

    private final PayrollCalculationStrategy payrollCalculationStrategy;

    public PayrollServiceImpl(PayrollCalculationStrategy payrollCalculationStrategy) {
        this.payrollCalculationStrategy = payrollCalculationStrategy;
    }

    @Override
    public List<PayrollItem> calculateMonthlyPayroll(Long employeeId, int year, int month) {
        // TODO: 勤怠データから残業時間・深夜時間を取得
        // ここではデモデータを使用
        BigDecimal baseSalary = new BigDecimal("300000");
        int overtimeHours = 20;
        int nightHours = 10;

        return payrollCalculationStrategy.calculate(baseSalary, overtimeHours, nightHours);
    }
}
