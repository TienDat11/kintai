package com.company.kintai.employee.domain;

public record EmergencyContact(
        Long id,
        Long employeeId,
        String name,
        String relationship,
        String phone,
        String address,
        Short priority) {}
