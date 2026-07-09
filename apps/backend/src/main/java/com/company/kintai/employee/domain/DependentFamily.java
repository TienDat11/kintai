package com.company.kintai.employee.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DependentFamily(
        Long id,
        Long employeeId,
        String fullName,
        String fullNameKana,
        String relationship,
        LocalDate birthDate,
        String socialInsuranceStatus,
        BigDecimal income) {}
