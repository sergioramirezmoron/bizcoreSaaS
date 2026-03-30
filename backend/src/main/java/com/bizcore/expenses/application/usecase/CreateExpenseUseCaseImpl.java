package com.bizcore.expenses.application.usecase;

import com.bizcore.expenses.application.dto.ExpenseRequest;
import com.bizcore.expenses.application.dto.ExpenseResponse;
import com.bizcore.expenses.domain.model.Expense;
import com.bizcore.expenses.domain.model.ExpenseStatus;
import com.bizcore.expenses.domain.port.in.CreateExpenseUseCase;
import com.bizcore.expenses.domain.port.out.ExpenseRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateExpenseUseCaseImpl implements CreateExpenseUseCase {

    private final ExpenseRepositoryPort expenseRepository;

    @Override
    @Transactional
    public ExpenseResponse create(UUID tenantId, UUID userId, ExpenseRequest request) {
        int year = LocalDate.now().getYear();
        long seq  = expenseRepository.countByTenantIdAndYear(tenantId, year) + 1;
        String expenseNumber = "EXP-%d-%05d".formatted(year, seq);

        Instant now = Instant.now();
        Expense expense = new Expense(
                UUID.randomUUID(), tenantId, request.branchId(),
                expenseNumber, request.category(), request.description(),
                request.amount(), ExpenseStatus.PENDING,
                request.dueDate(), null,
                request.supplierName(), request.attachmentUrl(), request.notes(),
                userId, now, now
        );

        return ExpenseResponse.from(expenseRepository.save(expense));
    }
}
