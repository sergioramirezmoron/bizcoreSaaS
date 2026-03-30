package com.bizcore.catalog.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Category(
        UUID id,
        UUID tenantId,
        String name,
        UUID parentId,      // null = categoría raíz
        String color,       // hex p.ej. "#FF5733"
        int sortOrder,
        boolean active,
        Instant createdAt
) {}
