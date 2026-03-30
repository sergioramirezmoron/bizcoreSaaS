package com.bizcore.expenses.application.usecase;

import com.bizcore.expenses.domain.model.Expense;
import com.bizcore.expenses.domain.model.ExpenseStatus;
import com.bizcore.expenses.domain.port.in.CancelExpenseUseCase;
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
public class CancelExpenseUseCaseImpl implements CancelExpenseUseCase {

    private final ExpenseRepositoryPort expenseRepository;

    @Override
    @Transactional
    public void cancel(UUID tenantId, UUID expenseId) {
        Expense existing = expenseRepository.findById(tenantId, expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado"));

        if (existing.status() == ExpenseStatus.CANCELLED) {
            throw new BusinessException("El gasto ya está cancelado");
        }
        if (existing.status() == ExpenseStatus.PAID) {
            throw new BusinessException("No se puede cancelar un gasto ya pagado");
        }

        Expense cancelled = new Expense(
                existing.id(), existing.tenantId(), existing.branchId(),
                existing.expenseNumber(), existing.category(), existing.description(),
                existing.amount(), ExpenseStatus.CANCELLED,
                existing.dueDate(), existing.paidAt(),
                existing.supplierName(), existing.attachmentUrl(), existing.notes(),
                existing.createdById(), existing.createdAt(), Instant.now()
        );

        expenseRepository.save(cancelled);
    }
}
