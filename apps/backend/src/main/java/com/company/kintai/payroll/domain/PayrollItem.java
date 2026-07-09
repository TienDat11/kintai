package com.company.kintai.payroll.domain;

import java.math.BigDecimal;

public record PayrollItem(
        String name,
        BigDecimal amount,
        String description) {}
