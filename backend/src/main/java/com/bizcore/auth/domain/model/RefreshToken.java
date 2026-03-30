package com.bizcore.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad de dominio para refresh tokens.
 * Solo se almacena el hash SHA-256, nunca el valor en texto plano.
 */
public record RefreshToken(
        UUID id,
        UUID userId,
        String tokenHash,
        String ipAddress,
        String userAgent,
        Instant expiresAt,
        Instant revokedAt   // null si está activo
) {
    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !isRevoked() && !isExpired();
    }
}
