package com.company.kintai.attendance.dto;

import jakarta.validation.constraints.NotNull;

public record CheckInRequest(@NotNull Long employeeId) {}
