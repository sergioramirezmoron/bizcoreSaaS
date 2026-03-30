package com.bizcore.auth.application.usecase;

import com.bizcore.auth.application.dto.LoginRequest;
import com.bizcore.auth.application.dto.TokenResponse;
import com.bizcore.auth.domain.model.AuthUser;
import com.bizcore.auth.domain.model.RefreshToken;
import com.bizcore.auth.domain.port.in.LoginUseCase;
import com.bizcore.auth.domain.port.out.JwtPort;
import com.bizcore.auth.domain.port.out.PasswordEncoderPort;
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
public class LoginUseCaseImpl implements LoginUseCase {

    private final UserRepositoryPort userRepo;
    private final RefreshTokenRepositoryPort refreshTokenRepo;
    private final JwtPort jwtPort;
    private final PasswordEncoderPort passwordEncoder;

    @Value("${bizcore.jwt.access-token-expiration:900}")
    private long accessTokenExpirationSeconds;

    @Value("${bizcore.jwt.refresh-token-expiration-days:30}")
    private long refreshTokenExpirationDays;

    @Override
    public TokenResponse login(LoginRequest request) {
        AuthUser user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new BusinessException("Credenciales incorrectas");
        }

        if (!user.active()) {
            throw new ForbiddenException("La cuenta está desactivada");
        }

        String accessToken = jwtPort.generateAccessToken(
                user.id(), user.tenantId(), user.email(), user.role()
        );
        String refreshTokenValue = jwtPort.generateRefreshTokenValue(user.id());

        RefreshToken refreshToken = new RefreshToken(
                null,
                user.id(),
                hashToken(refreshTokenValue),
                request.ipAddress(),
                request.userAgent(),
                Instant.now().plus(refreshTokenExpirationDays, ChronoUnit.DAYS),
                null
        );
        refreshTokenRepo.save(refreshToken);

        return TokenResponse.bearer(accessToken, refreshTokenValue, accessTokenExpirationSeconds);
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
