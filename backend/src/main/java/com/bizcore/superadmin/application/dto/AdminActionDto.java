package com.bizcore.superadmin.application.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminActionDto(
        UUID id,
        String actionType,
        String description,
        UUID performedBy,
        Instant createdAt
) {}
