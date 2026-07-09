package com.company.kintai.employee.domain;

public record BankAccount(
        Long id,
        Long employeeId,
        String bankCode,
        String branchCode,
        String accountType,
        String accountNumberMasked,
        String accountHolder,
        String accountHolderKana,
        Boolean isPrimary) {}
