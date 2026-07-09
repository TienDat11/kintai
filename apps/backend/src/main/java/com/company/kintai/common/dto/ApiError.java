package com.company.kintai.common.dto;

import java.util.List;

public record ApiError(String code, String message, List<FieldError> fieldErrors) {}
