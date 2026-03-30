package com.bizcore.auth.domain.port.out;

import com.bizcore.auth.domain.model.AuthUser;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    Optional<AuthUser> findByEmail(String email);
    Optional<AuthUser> findById(UUID id);
    boolean existsByEmail(String email);
    AuthUser save(AuthUser user);
}
