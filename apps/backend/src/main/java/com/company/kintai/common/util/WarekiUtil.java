package com.company.kintai.common.util;

import java.time.LocalDate;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class WarekiUtil {

    private static final DateTimeFormatter WAREKI_FORMATTER =
            DateTimeFormatter.ofPattern("令和GG年MM月DD日", Locale.JAPAN);

    private WarekiUtil() {}

    /**
     * 西暦を和暦（令和）に変換
     *
     * @param date 西暦日付
     * @return 和暦文字列 (例: 令和06年06月17日)
     */
    public static String toWareki(LocalDate date) {
        if (date == null) {
            return "";
        }
        JapaneseDate japaneseDate = JapaneseDate.from(date);
        return WAREKI_FORMATTER.format(japaneseDate);
    }

    /**
     * 西暦を和暦（令和）に変換
     *
     * @param date 西暦日付
     * @return 和暦文字列 (例: 令和06年06月17日)
     */
    public static String toWareki(JapaneseDate date) {
        if (date == null) {
            return "";
        }
        return WAREKI_FORMATTER.format(date);
    }
}
