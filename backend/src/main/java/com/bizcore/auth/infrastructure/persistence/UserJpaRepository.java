package com.bizcore.auth.infrastructure.persistence;

import com.bizcore.shared.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserJpaEntity> findFirstByTenantIdAndRole(UUID tenantId, UserRole role);
}
