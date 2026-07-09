package com.company.kintai.employee.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record Contract(
        Long id,
        Long employeeId,
        String contractType,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal baseSalary,
        String paymentType) {}
