package com.bizcore.auth.application.usecase;

import com.bizcore.auth.domain.model.RefreshToken;
import com.bizcore.auth.domain.port.in.LogoutUseCase;
import com.bizcore.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.bizcore.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepo;

    @Override
    public void logout(String refreshToken) {
        String tokenHash = hashToken(refreshToken);

        RefreshToken existing = refreshTokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException("Refresh token inválido"));

        if (existing.isRevoked()) {
            return; // Ya estaba revocado, no hay que hacer nada
        }

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
