package com.bizcore.company.domain.model;

import java.util.UUID;

public record BusinessType(
        UUID id,
        String code,
        String name,
        String description,
        String icon
) {}
