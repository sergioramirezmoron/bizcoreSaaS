package com.bizcore.auth.application.dto;

import com.bizcore.shared.domain.UserRole;

import java.util.UUID;

public record UserInfoResponse(
        UUID id,
        UUID tenantId,
        String email,
        String firstName,
        String lastName,
        UserRole role
) {}
