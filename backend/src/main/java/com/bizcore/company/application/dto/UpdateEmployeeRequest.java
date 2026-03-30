package com.bizcore.company.application.dto;

import com.bizcore.shared.domain.UserRole;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeRequest(
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        String phone,
        UserRole role
) {}
