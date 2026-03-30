package com.bizcore.expenses.domain.port.in;

import com.bizcore.expenses.application.dto.ExpenseRequest;
import com.bizcore.expenses.application.dto.ExpenseResponse;

import java.util.UUID;

public interface UpdateExpenseUseCase {
    ExpenseResponse update(UUID tenantId, UUID expenseId, ExpenseRequest request);
}
