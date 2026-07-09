package com.company.kintai.leave.domain;

import java.math.BigDecimal;

public record LeaveType(
        Long id,
        String code,
        String name,
        BigDecimal daysPerYear,
        Boolean isPaid) {}