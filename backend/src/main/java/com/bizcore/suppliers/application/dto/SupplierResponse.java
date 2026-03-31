package com.bizcore.suppliers.application.dto;

import com.bizcore.suppliers.domain.model.Supplier;

import java.time.Instant;
import java.util.UUID;

public record SupplierResponse(
        UUID id,
        String name,
        String contactName,
        String email,
        String phone,
        String address,
        String website,
        String notes,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static SupplierResponse from(Supplier s) {
        return new SupplierResponse(
                s.id(), s.name(), s.contactName(), s.email(),
                s.phone(), s.address(), s.website(), s.notes(),
                s.active(), s.createdAt(), s.updatedAt()
        );
    }
}
