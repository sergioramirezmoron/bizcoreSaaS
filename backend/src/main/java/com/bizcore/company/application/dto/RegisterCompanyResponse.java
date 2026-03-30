package com.bizcore.company.application.dto;

import java.util.UUID;

public record RegisterCompanyResponse(
        UUID companyId,
        String companyName,
        UUID ownerId,
        String ownerEmail,
        String ownerFirstName,
        String ownerLastName
) {}
