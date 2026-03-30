package com.bizcore.expenses.infrastructure.persistence;

import com.bizcore.expenses.domain.model.Expense;
import com.bizcore.expenses.domain.model.ExpenseCategory;
import com.bizcore.expenses.domain.model.ExpenseStatus;
import com.bizcore.expenses.domain.port.out.ExpenseRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ExpenseRepositoryAdapter implements ExpenseRepositoryPort {

    private final ExpenseJpaRepository jpaRepository;

    @Override
    public Expense save(Expense expense) {
        return jpaRepository.save(ExpenseJpaEntity.from(expense)).toDomain();
    }

    @Override
    public Optional<Expense> findById(UUID tenantId, UUID expenseId) {
        return jpaRepository.findByIdAndTenantId(expenseId, tenantId).map(ExpenseJpaEntity::toDomain);
    }

    @Override
    public Page<Expense> findAll(UUID tenantId, UUID branchId, ExpenseCategory category,
                                  ExpenseStatus status, LocalDate from, LocalDate to, Pageable pageable) {
        return jpaRepository.search(tenantId, branchId, category, status, from, to, pageable)
                .map(ExpenseJpaEntity::toDomain);
    }

    @Override
    public long countByTenantIdAndYear(UUID tenantId, int year) {
        return jpaRepository.countByTenantIdAndYear(tenantId, year);
    }
}
