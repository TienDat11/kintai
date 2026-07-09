package com.company.kintai.common.constant;

public enum ErrorCode {
    VALIDATION_ERROR("E001"),
    UNAUTHORIZED("E002"),
    FORBIDDEN("E003"),
    NOT_FOUND("E004"),
    DUPLICATE("E005"),
    BUSINESS_RULE_VIOLATION("E006"),
    INSUFFICIENT_LEAVE_BALANCE("E101"),
    ALREADY_CHECKED_IN("E102"),
    INTERNAL_ERROR("E999");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public String messageKey() {
        return "error." + code;
    }
}
