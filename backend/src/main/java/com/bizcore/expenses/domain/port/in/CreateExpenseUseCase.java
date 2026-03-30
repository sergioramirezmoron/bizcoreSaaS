package com.bizcore.expenses.domain.port.in;

import com.bizcore.expenses.application.dto.ExpenseRequest;
import com.bizcore.expenses.application.dto.ExpenseResponse;

import java.util.UUID;

public interface CreateExpenseUseCase {
    ExpenseResponse create(UUID tenantId, UUID userId, ExpenseRequest request);
}
