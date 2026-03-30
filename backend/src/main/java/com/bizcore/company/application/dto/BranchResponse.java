package com.bizcore.company.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BranchResponse(
        UUID id,
        UUID tenantId,
        String name,
        String phone,
        String address,
        boolean main,
        OffsetDateTime createdAt
) {}
