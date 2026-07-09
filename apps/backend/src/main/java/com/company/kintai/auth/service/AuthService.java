package com.company.kintai.auth.service;

import com.company.kintai.auth.dto.AuthRequest;
import com.company.kintai.auth.dto.AuthResponse;
import com.company.kintai.auth.dto.RefreshTokenRequest;

public interface AuthService {

    AuthResponse login(AuthRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}
