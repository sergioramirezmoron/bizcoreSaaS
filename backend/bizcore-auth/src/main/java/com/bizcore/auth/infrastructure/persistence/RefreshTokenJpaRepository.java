package com.bizcore.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshTokenJpaEntity t SET t.revokedAt = :now WHERE t.userId = :userId AND t.revokedAt IS NULL")
    void revokeAllByUserId(UUID userId, Instant now);

    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity t WHERE t.expiresAt < :now")
    void deleteExpiredBefore(Instant now);
}
