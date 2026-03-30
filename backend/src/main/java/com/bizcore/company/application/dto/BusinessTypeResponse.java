package com.bizcore.company.application.dto;

import java.util.UUID;

public record BusinessTypeResponse(
        UUID id,
        String code,
        String name,
        String description,
        String icon
) {}
