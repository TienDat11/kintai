package com.company.kintai.payroll.service;

import com.company.kintai.payroll.domain.PayrollItem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StandardPayrollCalculation implements PayrollCalculationStrategy {

    private static final BigDecimal OVERTIME_RATE = new BigDecimal("1.25");
    private static final BigDecimal NIGHT_RATE = new BigDecimal("1.25");
    private static final BigDecimal HOURLY_BASE_DIVISOR = new BigDecimal("160");

    @Override
    public List<PayrollItem> calculate(BigDecimal baseSalary, int overtimeHours, int nightHours) {
        var items = new ArrayList<PayrollItem>();

        // 基本給
        items.add(new PayrollItem("基本給", baseSalary, "月額基本給"));

        // 時給計算（基本給 ÷ 160時間）
        BigDecimal hourlyRate = baseSalary.divide(HOURLY_BASE_DIVISOR, 10, RoundingMode.HALF_UP);

        // 残業代（25%増）
        BigDecimal overtimePay =
                hourlyRate
                        .multiply(BigDecimal.valueOf(overtimeHours))
                        .multiply(OVERTIME_RATE)
                        .setScale(0, RoundingMode.HALF_UP);
        items.add(new PayrollItem("残業代", overtimePay, "25%増残業代"));

        // 深夜手当（25%増）
        BigDecimal nightPay =
                hourlyRate
                        .multiply(BigDecimal.valueOf(nightHours))
                        .multiply(NIGHT_RATE)
                        .setScale(0, RoundingMode.HALF_UP);
        items.add(new PayrollItem("深夜手当", nightPay, "22:00-5:00 深夜手当"));

        return items;
    }
}
