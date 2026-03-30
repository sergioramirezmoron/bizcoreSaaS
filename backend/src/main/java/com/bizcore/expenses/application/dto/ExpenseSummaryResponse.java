package com.bizcore.expenses.application.dto;

import com.bizcore.expenses.domain.model.Expense;
import com.bizcore.expenses.domain.model.ExpenseCategory;
import com.bizcore.expenses.domain.model.ExpenseStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseSummaryResponse(
        UUID id,
        String expenseNumber,
        ExpenseCategory category,
        String description,
        BigDecimal amount,
        ExpenseStatus status,
        LocalDate dueDate,
        String supplierName
) {
    public static ExpenseSummaryResponse from(Expense e) {
        return new ExpenseSummaryResponse(
                e.id(), e.expenseNumber(), e.category(), e.description(),
                e.amount(), e.status(), e.dueDate(), e.supplierName()
        );
    }
}
