package com.company.kintai.payroll.service;

import com.company.kintai.payroll.domain.PayrollItem;
import java.math.BigDecimal;
import java.util.List;

public interface PayrollService {

    List<PayrollItem> calculateMonthlyPayroll(Long employeeId, int year, int month);
}
