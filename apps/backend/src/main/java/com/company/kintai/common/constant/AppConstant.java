package com.company.kintai.common.constant;

import java.time.ZoneId;

public final class AppConstant {

    private AppConstant() {}

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final ZoneId TOKYO_ZONE = ZoneId.of("Asia/Tokyo");
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
