package com.bizcore.auth.domain.model;

import com.bizcore.shared.domain.UserRole;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad de dominio del módulo auth.
 * Representa únicamente los campos necesarios para autenticación.
 * Sin dependencias de Spring ni JPA.
 */
public record AuthUser(
        UUID id,
        UUID tenantId,      // null para SUPER_ADMIN
        String email,
        String passwordHash,
        String firstName,
        String lastName,
        UserRole role,
        boolean active,
        OffsetDateTime createdAt
) {
    public boolean isSuperAdmin() {
        return role == UserRole.SUPER_ADMIN;
    }
}
