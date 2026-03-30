package com.bizcore.company.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegisterCompanyRequest(
        @NotBlank @Size(max = 255) String companyName,
        UUID businessTypeId,
        String timezone,
        @NotBlank @Email String ownerEmail,
        @NotBlank @Size(min = 8, max = 100) String ownerPassword,
        @NotBlank @Size(max = 100) String ownerFirstName,
        @NotBlank @Size(max = 100) String ownerLastName,
        String ownerPhone
) {}
