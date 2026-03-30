package com.bizcore.sales.application.dto;

import com.bizcore.sales.domain.model.Customer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String notes,
        BigDecimal totalSpent,
        int orderCount,
        Instant createdAt
) {
    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.id(), c.firstName(), c.lastName(),
                c.email(), c.phone(), c.notes(), c.totalSpent(), c.orderCount(), c.createdAt());
    }
}
