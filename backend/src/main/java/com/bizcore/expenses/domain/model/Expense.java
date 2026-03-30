package com.bizcore.expenses.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Expense(
        UUID id,
        UUID tenantId,
        UUID branchId,
        String expenseNumber,
        ExpenseCategory category,
        String description,
        BigDecimal amount,
        ExpenseStatus status,
        LocalDate dueDate,
        Instant paidAt,
        String supplierName,
        String attachmentUrl,
        String notes,
        UUID createdById,
        Instant createdAt,
        Instant updatedAt
) {}
