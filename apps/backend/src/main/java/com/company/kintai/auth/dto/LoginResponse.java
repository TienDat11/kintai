package com.company.kintai.auth.dto;

public record LoginResponse(
        String accessToken, String refreshToken, String role, String username) {}
