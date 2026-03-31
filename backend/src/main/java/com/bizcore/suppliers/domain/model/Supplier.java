package com.bizcore.suppliers.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Supplier(
        UUID id,
        UUID tenantId,
        String name,
        String contactName,
        String email,
        String phone,
        String address,
        String website,
        String notes,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
