package com.company.kintai.leave.dto;

import jakarta.validation.constraints.NotNull;

public record ApprovalRequest(@NotNull Long approverId, @NotNull String action) {}
