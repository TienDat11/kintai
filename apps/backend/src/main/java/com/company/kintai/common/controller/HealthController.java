package com.company.kintai.common.controller;

import com.company.kintai.common.dto.ApiResponse;
import java.time.Instant;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.ok(
                Map.of("status", "UP", "timestamp", Instant.now()));
    }
}
