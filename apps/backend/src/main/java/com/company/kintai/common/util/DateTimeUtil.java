package com.company.kintai.common.util;

import com.company.kintai.common.constant.AppConstant;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(AppConstant.DATE_FORMAT);
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(AppConstant.DATETIME_FORMAT);

    private DateTimeUtil() {}

    /** 日付文字列をフォーマット */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    /** 日時文字列をフォーマット */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }

    /** 文字列を日付にパース */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /** 文字列を日時にパース */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    /** 勤務時間の計算（時間単位、小数点以下切り上げ） */
    public static double calculateWorkHours(LocalDateTime checkIn, LocalDateTime checkOut, int breakMinutes) {
        if (checkIn == null || checkOut == null) {
            return 0.0;
        }
        Duration duration = Duration.between(checkIn, checkOut);
        long totalMinutes = duration.toMinutes() - breakMinutes;
        if (totalMinutes < 0) {
            totalMinutes = 0;
        }
        return Math.ceil(totalMinutes / 60.0 * 10.0) / 10.0;
    }

    /** Instantを東京タイムゾーンのLocalDateTimeに変換 */
    public static LocalDateTime toTokyoLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(AppConstant.TOKYO_ZONE).toLocalDateTime();
    }
}
