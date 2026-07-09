package com.company.kintai.leave.domain;

import java.math.BigDecimal;

public record LeaveBalance(
        Long id,
        Long employeeId,
        Integer fiscalYear,
        Long leaveTypeId,
        BigDecimal grantedDays,
        BigDecimal usedDays,
        BigDecimal remainingDays) {}