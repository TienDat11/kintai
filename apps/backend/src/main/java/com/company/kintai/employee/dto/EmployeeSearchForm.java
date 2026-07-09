package com.company.kintai.employee.dto;

import com.company.kintai.common.constant.AppConstant;

public record EmployeeSearchForm(String keyword, Long departmentId, String status, Integer page, Integer size) {

    public int pageOrDefault() {
        return page == null ? 0 : page;
    }

    public int sizeOrDefault() {
        if (size == null) {
            return AppConstant.DEFAULT_PAGE_SIZE;
        }
        return Math.min(Math.max(size, 1), AppConstant.MAX_PAGE_SIZE);
    }
}
