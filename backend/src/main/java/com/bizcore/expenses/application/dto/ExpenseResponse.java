package com.bizcore.expenses.application.dto;

import com.bizcore.expenses.domain.model.Expense;
import com.bizcore.expenses.domain.model.ExpenseCategory;
import com.bizcore.expenses.domain.model.ExpenseStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponse(
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
) {
    public static ExpenseResponse from(Expense e) {
        return new ExpenseResponse(
                e.id(), e.tenantId(), e.branchId(), e.expenseNumber(),
                e.category(), e.description(), e.amount(), e.status(),
                e.dueDate(), e.paidAt(), e.supplierName(), e.attachmentUrl(),
                e.notes(), e.createdById(), e.createdAt(), e.updatedAt()
        );
    }
}
