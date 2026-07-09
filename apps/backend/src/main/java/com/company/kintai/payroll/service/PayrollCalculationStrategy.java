package com.company.kintai.payroll.service;

import com.company.kintai.payroll.domain.PayrollItem;
import java.math.BigDecimal;
import java.util.List;

public interface PayrollCalculationStrategy {

    List<PayrollItem> calculate(BigDecimal baseSalary, int overtimeHours, int nightHours);
}
