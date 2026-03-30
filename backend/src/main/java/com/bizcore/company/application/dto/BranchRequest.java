package com.bizcore.company.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BranchRequest(
        @NotBlank @Size(max = 255) String name,
        String phone,
        String address,
        boolean main
) {}
