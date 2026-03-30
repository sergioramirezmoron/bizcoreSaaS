package com.bizcore.expenses.application.usecase;

import com.bizcore.expenses.application.dto.ExpenseSummaryResponse;
import com.bizcore.expenses.domain.model.ExpenseCategory;
import com.bizcore.expenses.domain.model.ExpenseStatus;
import com.bizcore.expenses.domain.port.in.ListExpensesUseCase;
import com.bizcore.expenses.domain.port.out.ExpenseRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListExpensesUseCaseImpl implements ListExpensesUseCase {

    private final ExpenseRepositoryPort expenseRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseSummaryResponse> list(UUID tenantId, UUID branchId, ExpenseCategory category,
                                              ExpenseStatus status, LocalDate from, LocalDate to,
                                              Pageable pageable) {
        return expenseRepository.findAll(tenantId, branchId, category, status, from, to, pageable)
                .map(ExpenseSummaryResponse::from);
    }
}
