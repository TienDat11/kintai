package com.company.kintai.leave.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LeaveRequest(
        @NotNull Long employeeId,
        @NotNull Long leaveTypeId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        BigDecimal days,
        String reason) {}