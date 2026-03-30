package com.bizcore.expenses.application.usecase;

import com.bizcore.expenses.application.dto.ExpenseRequest;
import com.bizcore.expenses.application.dto.ExpenseResponse;
import com.bizcore.expenses.domain.model.Expense;
import com.bizcore.expenses.domain.model.ExpenseStatus;
import com.bizcore.expenses.domain.port.in.UpdateExpenseUseCase;
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
public class UpdateExpenseUseCaseImpl implements UpdateExpenseUseCase {

    private final ExpenseRepositoryPort expenseRepository;

    @Override
    @Transactional
    public ExpenseResponse update(UUID tenantId, UUID expenseId, ExpenseRequest request) {
        Expense existing = expenseRepository.findById(tenantId, expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado"));

        if (existing.status() != ExpenseStatus.PENDING) {
            throw new BusinessException("Solo se pueden editar gastos en estado PENDING");
        }

        Expense updated = new Expense(
                existing.id(), existing.tenantId(), request.branchId(),
                existing.expenseNumber(), request.category(), request.description(),
                request.amount(), existing.status(),
                request.dueDate(), existing.paidAt(),
                request.supplierName(), request.attachmentUrl(), request.notes(),
                existing.createdById(), existing.createdAt(), Instant.now()
        );

        return ExpenseResponse.from(expenseRepository.save(updated));
    }
}
