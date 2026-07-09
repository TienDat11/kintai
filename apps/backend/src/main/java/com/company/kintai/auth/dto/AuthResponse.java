package com.company.kintai.auth.dto;

public record AuthResponse(
        String accessToken, String refreshToken, String role, String username) {}
