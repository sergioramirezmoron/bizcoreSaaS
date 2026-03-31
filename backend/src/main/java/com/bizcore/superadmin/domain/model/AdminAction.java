package com.bizcore.superadmin.domain.model;

import java.time.Instant;
import java.util.UUID;

public record AdminAction(
        UUID id,
        UUID companyId,
        AdminActionType actionType,
        String description,
        String valueJson,
        UUID performedBy,
        Instant createdAt
) {}
