package com.company.kintai.employee.dto;

public record EmployeeResponse(
        Long id,
        String code,
        String fullName,
        String fullNameKana,
        String departmentName,
        String email,
        String phone,
        String status) {}