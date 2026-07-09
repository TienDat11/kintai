package com.company.kintai.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DateTimeUtilTest {

    @Test
    void should_format_date() {
        LocalDate date = LocalDate.of(2024, 6, 17);
        assertThat(DateTimeUtil.formatDate(date)).isEqualTo("2024-06-17");
    }

    @Test
    void should_format_datetime() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 6, 17, 14, 30, 0);
        assertThat(DateTimeUtil.formatDateTime(dateTime)).isEqualTo("2024-06-17 14:30:00");
    }

    @Test
    void should_return_empty_when_date_is_null() {
        assertThat(DateTimeUtil.formatDate(null)).isEmpty();
    }

    @Test
    void should_parse_date() {
        assertThat(DateTimeUtil.parseDate("2024-06-17")).isEqualTo(LocalDate.of(2024, 6, 17));
    }

    @Test
    void should_parse_datetime() {
        assertThat(DateTimeUtil.parseDateTime("2024-06-17 14:30:00"))
                .isEqualTo(LocalDateTime.of(2024, 6, 17, 14, 30, 0));
    }

    @Test
    void should_calculate_work_hours() {
        LocalDateTime checkIn = LocalDateTime.of(2024, 6, 17, 9, 0);
        LocalDateTime checkOut = LocalDateTime.of(2024, 6, 17, 18, 0);
        double hours = DateTimeUtil.calculateWorkHours(checkIn, checkOut, 60);
        assertThat(hours).isEqualTo(8.0);
    }
}
