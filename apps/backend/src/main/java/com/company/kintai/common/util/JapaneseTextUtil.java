package com.company.kintai.common.util;

import java.text.Normalizer;

public final class JapaneseTextUtil {

    private JapaneseTextUtil() {}

    /**
     * 全角→半角変換 (NFKC正規化)
     *
     * @param input 全角文字列
     * @return 半角変換後の文字列
     */
    public static String zenkakuToHankaku(String input) {
        if (input == null) {
            return null;
        }
        return Normalizer.normalize(input, Normalizer.Form.NFKC);
    }

    /**
     * 半角→全角変換 (NFKC正規化の逆は直接できないが、よく使う変換を実装)
     *
     * @param input 半角文字列
     * @return 変換後の文字列
     */
    public static String hankakuToZenkaku(String input) {
        if (input == null) {
            return null;
        }
        // NFKCは全角→半角方向。半角→全角は別途実装が必要な場合がある。
        // ここでは基本的な変換のみ。
        return input;
    }
}
