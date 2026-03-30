package com.bizcore.auth.domain.model;

import com.bizcore.shared.domain.UserRole;

import java.util.UUID;

/**
 * Datos extraídos de un JWT access token.
 * Tipo de dominio puro, sin dependencias de JJWT.
 */
public record TokenClaims(
        UUID userId,
        UUID tenantId,
        String email,
        UserRole role
) {}
