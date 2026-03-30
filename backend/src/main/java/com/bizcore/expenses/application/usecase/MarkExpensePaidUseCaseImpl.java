package com.bizcore.expenses.application.usecase;

import com.bizcore.expenses.application.dto.ExpenseResponse;
import com.bizcore.expenses.domain.model.Expense;
import com.bizcore.expenses.domain.model.ExpenseStatus;
import com.bizcore.expenses.domain.port.in.MarkExpensePaidUseCase;
import com.bizcore.expenses.domain.port.out.ExpenseRepositoryPort;
import com.bizcore.shared.exception.BusinessException;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarkExpensePaidUseCaseImpl implements MarkExpensePaidUseCase {

    private final ExpenseRepositoryPort expenseRepository;

    @Override
    @Transactional
    public ExpenseResponse markPaid(UUID tenantId, UUID expenseId) {
        Expense existing = expenseRepository.findById(tenantId, expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado"));

        if (existing.status() != ExpenseStatus.PENDING) {
            throw new BusinessException("Solo se pueden marcar como pagados los gastos en estado PENDING");
        }

        Instant now = Instant.now();
        Expense paid = new Expense(
                existing.id(), existing.tenantId(), existing.branchId(),
                existing.expenseNumber(), existing.category(), existing.description(),
                existing.amount(), ExpenseStatus.PAID,
                existing.dueDate(), now,
                existing.supplierName(), existing.attachmentUrl(), existing.notes(),
                existing.createdById(), existing.createdAt(), now
        );

        return ExpenseResponse.from(expenseRepository.save(paid));
    }
}
