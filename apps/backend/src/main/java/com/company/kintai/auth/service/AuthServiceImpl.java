package com.company.kintai.auth.service;

import com.company.kintai.auth.dto.AuthRequest;
import com.company.kintai.auth.dto.AuthResponse;
import com.company.kintai.auth.dto.RefreshTokenRequest;
import com.company.kintai.common.constant.ErrorCode;
import com.company.kintai.common.exception.BusinessException;
import com.company.kintai.common.security.JwtTokenProvider;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * BLOCKED: No real auth table exists for login. This is a stub — auth will need
 * an external IdP or a separate auth table (Phase 2+).
 * Currently only supports token refresh via existing refresh token.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        // BLOCKED: There is no table to store/verify passwords in the real schema.
        // Login authentication cannot work until a solution is in place.
        throw new BusinessException(ErrorCode.UNAUTHORIZED,
                "Login not available — no auth table in current schema");
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if (!jwtTokenProvider.validateToken(request.refreshToken())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid refresh token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(request.refreshToken());
        List<String> permissions = jwtTokenProvider.getPermissionsFromToken(request.refreshToken());

        String newAccessToken = jwtTokenProvider.generateAccessToken(username, permissions);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        return new AuthResponse(newAccessToken, newRefreshToken,
                String.join(",", permissions), username);
    }
}