package com.bizcore.suppliers.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 100) String contactName,
        @Size(max = 150) String email,
        @Size(max = 20) String phone,
        String address,
        @Size(max = 255) String website,
        String notes
) {}
