package com.bizcore.auth.infrastructure.security;

import com.bizcore.auth.domain.model.TokenClaims;
import com.bizcore.auth.domain.model.UserRole;
import com.bizcore.auth.domain.port.out.JwtPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAdapter implements JwtPort {

    private static final String CLAIM_TENANT_ID = "tenantId";
    private static final String CLAIM_EMAIL     = "email";
    private static final String CLAIM_ROLE      = "role";

    private final JwtProperties props;

    @Override
    public String generateAccessToken(UUID userId, UUID tenantId, String email, UserRole role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim(CLAIM_TENANT_ID, tenantId != null ? tenantId.toString() : null)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLE, role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(props.getAccessTokenExpiration(), ChronoUnit.SECONDS)))
                .signWith(secretKey())
                .compact();
    }

    @Override
    public String generateRefreshTokenValue(UUID userId) {
        // El refresh token es un JWT mínimo; el valor real se hashea antes de guardarlo
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(props.getRefreshTokenExpirationDays(), ChronoUnit.DAYS)))
                .signWith(secretKey())
                .compact();
    }

    @Override
    public TokenClaims extractClaims(String token) {
        Claims claims = parseClaims(token);
        UUID tenantIdRaw = claims.get(CLAIM_TENANT_ID, String.class) != null
                ? UUID.fromString(claims.get(CLAIM_TENANT_ID, String.class))
                : null;
        return new TokenClaims(
                UUID.fromString(claims.getSubject()),
                tenantIdRaw,
                claims.get(CLAIM_EMAIL, String.class),
                UserRole.valueOf(claims.get(CLAIM_ROLE, String.class))
        );
    }

    @Override
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
