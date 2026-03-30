package com.bizcore.company.domain.model;

import com.bizcore.shared.domain.UserRole;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Vista de dominio de un usuario en el contexto de la empresa.
 * No contiene el password hash (eso es responsabilidad de bizcore-auth).
 */
public record Employee(
        UUID id,
        UUID tenantId,
        String email,
        String firstName,
        String lastName,
        String phone,
        UserRole role,
        boolean active,
        OffsetDateTime createdAt
) {}
