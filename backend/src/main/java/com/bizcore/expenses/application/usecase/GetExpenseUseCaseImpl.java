package com.bizcore.expenses.application.usecase;

import com.bizcore.expenses.application.dto.ExpenseResponse;
import com.bizcore.expenses.domain.port.in.GetExpenseUseCase;
import com.bizcore.expenses.domain.port.out.ExpenseRepositoryPort;
import com.bizcore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetExpenseUseCaseImpl implements GetExpenseUseCase {

    private final ExpenseRepositoryPort expenseRepository;

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse get(UUID tenantId, UUID expenseId) {
        return expenseRepository.findById(tenantId, expenseId)
                .map(ExpenseResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado"));
    }
}
