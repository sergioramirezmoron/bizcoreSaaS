package com.bizcore.expenses.domain.port.out;

import com.bizcore.expenses.domain.model.Expense;
import com.bizcore.expenses.domain.model.ExpenseCategory;
import com.bizcore.expenses.domain.model.ExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepositoryPort {

    Expense save(Expense expense);

    Optional<Expense> findById(UUID tenantId, UUID expenseId);

    Page<Expense> findAll(UUID tenantId, UUID branchId, ExpenseCategory category,
                          ExpenseStatus status, LocalDate from, LocalDate to, Pageable pageable);

    long countByTenantIdAndYear(UUID tenantId, int year);
}
