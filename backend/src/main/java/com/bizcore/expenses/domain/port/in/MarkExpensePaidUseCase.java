package com.bizcore.expenses.domain.port.in;

import com.bizcore.expenses.application.dto.ExpenseResponse;

import java.util.UUID;

public interface MarkExpensePaidUseCase {
    ExpenseResponse markPaid(UUID tenantId, UUID expenseId);
}
