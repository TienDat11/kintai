package com.company.kintai.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JapaneseTextUtilTest {

    @Test
    void should_convert_zenkaku_to_hankaku() {
        String input = "ＡＢＣ１２３";
        String result = JapaneseTextUtil.zenkakuToHankaku(input);
        assertThat(result).isEqualTo("ABC123");
    }

    @Test
    void should_return_null_when_input_is_null() {
        assertThat(JapaneseTextUtil.zenkakuToHankaku(null)).isNull();
    }

    @Test
    void should_return_empty_when_input_is_empty() {
        assertThat(JapaneseTextUtil.zenkakuToHankaku("")).isEmpty();
    }

    @Test
    void should_not_change_already_hankaku() {
        String input = "Hello123";
        assertThat(JapaneseTextUtil.zenkakuToHankaku(input)).isEqualTo("Hello123");
    }
}
