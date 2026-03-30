package com.bizcore.sales.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        UUID branchId,
        UUID customerId,
        @NotEmpty @Valid List<OrderItemRequest> items,
        String notes
) {}
