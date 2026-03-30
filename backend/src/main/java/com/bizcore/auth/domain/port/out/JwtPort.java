package com.bizcore.auth.domain.port.out;

import com.bizcore.auth.domain.model.TokenClaims;
import com.bizcore.shared.domain.UserRole;

import java.util.UUID;

public interface JwtPort {
    String generateAccessToken(UUID userId, UUID tenantId, String email, UserRole role);
    String generateRefreshTokenValue(UUID userId);
    TokenClaims extractClaims(String token);
    boolean isValid(String token);
}
