package com.bizcore.auth.infrastructure.persistence;

import com.bizcore.auth.domain.model.RefreshToken;
import com.bizcore.auth.domain.port.out.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository jpaRepository;

    @Override
    public RefreshToken save(RefreshToken token) {
        RefreshTokenJpaEntity entity = toEntity(token);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        jpaRepository.revokeAllByUserId(userId, Instant.now());
    }

    @Override
    public void deleteExpired() {
        jpaRepository.deleteExpiredBefore(Instant.now());
    }

    private RefreshToken toDomain(RefreshTokenJpaEntity e) {
        return new RefreshToken(
                e.getId(),
                e.getUserId(),
                e.getTokenHash(),
                e.getIpAddress(),
                e.getUserAgent(),
                e.getExpiresAt(),
                e.getRevokedAt()
        );
    }

    private RefreshTokenJpaEntity toEntity(RefreshToken t) {
        return RefreshTokenJpaEntity.builder()
                .id(t.id())
                .userId(t.userId())
                .tokenHash(t.tokenHash())
                .ipAddress(t.ipAddress())
                .userAgent(t.userAgent())
                .expiresAt(t.expiresAt())
                .revokedAt(t.revokedAt())
                .build();
    }
}
