package com.bizcore.expenses.domain.port.in;

import com.bizcore.expenses.application.dto.ExpenseSummaryResponse;
import com.bizcore.expenses.domain.model.ExpenseCategory;
import com.bizcore.expenses.domain.model.ExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface ListExpensesUseCase {
    Page<ExpenseSummaryResponse> list(UUID tenantId, UUID branchId, ExpenseCategory category,
                                      ExpenseStatus status, LocalDate from, LocalDate to, Pageable pageable);
}
