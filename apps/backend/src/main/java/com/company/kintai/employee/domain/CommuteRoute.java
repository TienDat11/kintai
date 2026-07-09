package com.company.kintai.employee.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CommuteRoute(
        Long id,
        Long employeeId,
        String fromStation,
        String toStation,
        BigDecimal distanceKm,
        BigDecimal monthlyPassCost,
        LocalDate validFrom,
        LocalDate validTo) {}
