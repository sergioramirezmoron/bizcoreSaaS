package com.bizcore.company.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Branch(
        UUID id,
        UUID tenantId,
        String name,
        String phone,
        String address,
        boolean main,
        OffsetDateTime createdAt
) {}
