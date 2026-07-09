package com.company.kintai.employee.domain;

import java.time.LocalDate;

public record Employee(
        Long id,
        String code,
        String fullName,
        String fullNameKana,
        String email,
        String phone,
        String postalCode,
        LocalDate birthDate,
        LocalDate hireDate,
        Long departmentId,
        String departmentName,
        Long positionId,
        String status,
        String mynumberMasked) {}
