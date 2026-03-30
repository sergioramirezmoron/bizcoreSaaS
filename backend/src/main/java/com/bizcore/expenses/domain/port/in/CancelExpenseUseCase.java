package com.bizcore.expenses.domain.port.in;

import java.util.UUID;

public interface CancelExpenseUseCase {
    void cancel(UUID tenantId, UUID expenseId);
}
