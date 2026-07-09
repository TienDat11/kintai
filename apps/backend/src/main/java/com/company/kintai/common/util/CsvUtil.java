package com.company.kintai.common.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class CsvUtil {

    private static final Charset MS932;

    static {
        // MS932 (Shift_JIS拡張) が利用可能かチェック
        if (Charset.isSupported("MS932")) {
            MS932 = Charset.forName("MS932");
        } else {
            // フォールバック: Shift_JIS
            MS932 = StandardCharsets.UTF_8;
        }
    }

    private CsvUtil() {}

    /**
     * CSVをShift_JIS(MS932)で出力
     *
     * @param outputStream 出力先
     * @param headers ヘッダー行
     * @param rows データ行
     */
    public static void writeShiftJis(OutputStream outputStream, List<String> headers, List<List<String>> rows)
            throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, MS932)) {
            // ヘッダー
            writer.write(String.join(",", headers));
            writer.write("\r\n");

            // データ行
            for (List<String> row : rows) {
                writer.write(String.join(",", row));
                writer.write("\r\n");
            }
        }
    }

    /**
     * CSVを指定エンコーディングで出力
     */
    public static void writeCsv(
            OutputStream outputStream, Charset charset, List<String> headers, List<List<String>> rows)
            throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, charset)) {
            writer.write(String.join(",", headers));
            writer.write("\r\n");
            for (List<String> row : rows) {
                writer.write(String.join(",", row));
                writer.write("\r\n");
            }
        }
    }
}
