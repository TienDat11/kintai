package com.company.kintai.attendance.dto;

import jakarta.validation.constraints.NotNull;

public record CheckOutRequest(@NotNull Long employeeId, Integer breakMinutes) {}
