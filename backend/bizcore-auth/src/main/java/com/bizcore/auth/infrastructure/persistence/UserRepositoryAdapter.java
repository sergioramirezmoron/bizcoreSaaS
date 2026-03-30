package com.bizcore.auth.infrastructure.persistence;

import com.bizcore.auth.domain.model.AuthUser;
import com.bizcore.auth.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<AuthUser> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<AuthUser> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public AuthUser save(AuthUser user) {
        UserJpaEntity entity = toEntity(user);
        return toDomain(jpaRepository.save(entity));
    }

    private AuthUser toDomain(UserJpaEntity e) {
        return new AuthUser(
                e.getId(),
                e.getTenantId(),
                e.getEmail(),
                e.getPasswordHash(),
                e.getFirstName(),
                e.getLastName(),
                e.getRole(),
                e.isActive(),
                e.getCreatedAt()
        );
    }

    private UserJpaEntity toEntity(AuthUser u) {
        return UserJpaEntity.builder()
                .id(u.id())
                .tenantId(u.tenantId())
                .email(u.email())
                .passwordHash(u.passwordHash())
                .firstName(u.firstName())
                .lastName(u.lastName())
                .role(u.role())
                .active(u.active())
                .build();
    }
}
