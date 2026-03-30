package com.bizcore.company.application.dto;

import com.bizcore.shared.domain.UserRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        UserRole role,
        boolean active,
        OffsetDateTime createdAt
) {}
