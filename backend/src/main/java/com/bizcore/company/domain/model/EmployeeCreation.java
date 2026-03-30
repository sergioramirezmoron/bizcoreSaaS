package com.bizcore.company.domain.model;

import com.bizcore.shared.domain.UserRole;

import java.util.UUID;

/**
 * Comando de dominio para crear un usuario con contraseña.
 * El passwordHash ya viene codificado desde la capa application.
 */
public record EmployeeCreation(
        UUID tenantId,
        String email,
        String passwordHash,
        String firstName,
        String lastName,
        String phone,
        UserRole role
) {}
