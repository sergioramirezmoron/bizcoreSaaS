package com.bizcore.auth.application.usecase;

import com.bizcore.auth.application.dto.RefreshRequest;
import com.bizcore.auth.application.dto.TokenResponse;
import com.bizcore.auth.domain.model.AuthUser;
import com.bizcore.auth.domain.model.RefreshToken;
import com.bizcore.auth.domain.port.in.RefreshTokenUseCase;
import com.bizcore.auth.domain.port.out.JwtPort;
import com.bizcore.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.bizcore.auth.domain.port.out.UserRepositoryPort;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.shared.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepo;
    private final UserRepositoryPort userRepo;
    private final JwtPort jwtPort;

    @Value("${bizcore.jwt.access-token-expiration:900}")
    private long accessTokenExpirationSeconds;

    @Value("${bizcore.jwt.refresh-token-expiration-days:30}")
    private long refreshTokenExpirationDays;

    @Override
    public TokenResponse refresh(RefreshRequest request) {
        String tokenHash = hashToken(request.refreshToken());

        RefreshToken existing = refreshTokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException("Refresh token inválido"));

        if (!existing.isValid()) {
            throw new BusinessException("Refresh token expirado o revocado");
        }

        AuthUser user = userRepo.findById(existing.userId())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if (!user.active()) {
            throw new ForbiddenException("La cuenta está desactivada");
        }

        // Rotación: revocar el token actual y emitir uno nuevo
        RefreshToken revoked = new RefreshToken(
                existing.id(),
                existing.userId(),
                existing.tokenHash(),
                existing.ipAddress(),
                existing.userAgent(),
                existing.expiresAt(),
                Instant.now()
        );
        refreshTokenRepo.save(revoked);

        String newAccessToken = jwtPort.generateAccessToken(
                user.id(), user.tenantId(), user.email(), user.role()
        );
        String newRefreshTokenValue = jwtPort.generateRefreshTokenValue(user.id());

        RefreshToken newRefreshToken = new RefreshToken(
                null,
                user.id(),
                hashToken(newRefreshTokenValue),
                existing.ipAddress(),
                existing.userAgent(),
                Instant.now().plus(refreshTokenExpirationDays, ChronoUnit.DAYS),
                null
        );
        refreshTokenRepo.save(newRefreshToken);

        return TokenResponse.bearer(newAccessToken, newRefreshTokenValue, accessTokenExpirationSeconds);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
