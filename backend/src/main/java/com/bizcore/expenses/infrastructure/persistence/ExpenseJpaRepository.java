package com.bizcore.expenses.infrastructure.persistence;

import com.bizcore.expenses.domain.model.ExpenseCategory;
import com.bizcore.expenses.domain.model.ExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseJpaRepository extends JpaRepository<ExpenseJpaEntity, UUID> {

    Optional<ExpenseJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    @Query("""
            SELECT e FROM ExpenseJpaEntity e
            WHERE e.tenantId = :tenantId
              AND (:branchId   IS NULL OR e.branchId  = :branchId)
              AND (:category   IS NULL OR e.category  = :category)
              AND (:status     IS NULL OR e.status    = :status)
              AND (:from       IS NULL OR e.dueDate  >= :from)
              AND (:to         IS NULL OR e.dueDate  <= :to)
            """)
    Page<ExpenseJpaEntity> search(@Param("tenantId") UUID tenantId,
                                  @Param("branchId") UUID branchId,
                                  @Param("category") ExpenseCategory category,
                                  @Param("status")   ExpenseStatus status,
                                  @Param("from")     LocalDate from,
                                  @Param("to")       LocalDate to,
                                  Pageable pageable);

    @Query(value = """
            SELECT COUNT(*) FROM expenses
            WHERE tenant_id = :tenantId
              AND EXTRACT(YEAR FROM created_at) = :year
            """, nativeQuery = true)
    long countByTenantIdAndYear(@Param("tenantId") UUID tenantId, @Param("year") int year);
}
