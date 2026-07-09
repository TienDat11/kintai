package com.company.kintai.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmployeeRequest(
        @NotBlank @Size(max = 50) String code,
        @NotBlank @Size(max = 100) String fullName,
        @Size(max = 100) String fullNameKana,
        @NotNull Long departmentId,
        @Email String email,
        @Pattern(regexp = "^0\\d{1,4}-?\\d{1,4}-?\\d{3,4}$", message = "{validation.phone.jp}")
                String phone,
        @Pattern(regexp = "^\\d{3}-?\\d{4}$") String postalCode) {}
