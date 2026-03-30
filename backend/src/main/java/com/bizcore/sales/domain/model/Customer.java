package com.bizcore.sales.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Customer(
        UUID id,
        UUID tenantId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String notes,
        BigDecimal totalSpent,
        int orderCount,
        Instant createdAt
) {}
