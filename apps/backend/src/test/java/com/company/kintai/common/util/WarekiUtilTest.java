package com.company.kintai.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class WarekiUtilTest {

    @Test
    void should_convert_to_wareki() {
        // 2024年6月17日 = 令和6年6月17日
        LocalDate date = LocalDate.of(2024, 6, 17);
        String result = WarekiUtil.toWareki(date);
        assertThat(result).contains("令和");
        assertThat(result).contains("6月");
        assertThat(result).contains("17日");
    }

    @Test
    void should_return_empty_when_date_is_null() {
        assertThat(WarekiUtil.toWareki((LocalDate) null)).isEmpty();
    }
}
