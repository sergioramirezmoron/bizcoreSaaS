package com.bizcore.company.application.dto;

import jakarta.validation.constraints.Size;

public record UpdateCompanyRequest(
        @Size(max = 255) String name,
        String taxId,
        String phone,
        String address,
        String timezone,
        String logoUrl
) {}
