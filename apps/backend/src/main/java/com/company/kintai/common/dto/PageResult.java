package com.company.kintai.common.dto;

import java.util.List;

public record PageResult<T>(
        List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <T> PageResult<T> of(List<T> content, int page, int size, long total) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);
        return new PageResult<>(content, page, size, total, totalPages);
    }
}
