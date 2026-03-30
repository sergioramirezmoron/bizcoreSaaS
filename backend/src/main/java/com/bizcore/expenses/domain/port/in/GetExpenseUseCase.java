package com.bizcore.expenses.domain.port.in;

import com.bizcore.expenses.application.dto.ExpenseResponse;

import java.util.UUID;

public interface GetExpenseUseCase {
    ExpenseResponse get(UUID tenantId, UUID expenseId);
}
