package com.bizcore.catalog.application.dto;

import com.bizcore.catalog.domain.model.Category;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        UUID parentId,
        String color,
        int sortOrder,
        boolean active,
        Instant createdAt
) {
    public static CategoryResponse from(Category c) {
        return new CategoryResponse(c.id(), c.name(), c.parentId(), c.color(), c.sortOrder(), c.active(), c.createdAt());
    }
}
